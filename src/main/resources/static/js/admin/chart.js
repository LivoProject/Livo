/* =========================================================
   chart-tabs.js — 탭 전환형 공용 차트/표 로더
   엔드포인트(그대로 사용):
     - /admin/chart/api/signups/monthly
     - /admin/chart/api/revenue/monthly
     - /admin/chart/api/instructors/ops
     - /admin/chart/api/lectures/top
   ========================================================= */

// ---------- 유틸 ----------
const $  = (s, p=document) => p.querySelector(s);
const $$ = (s, p=document) => [...p.querySelectorAll(s)];
const num = new Intl.NumberFormat('ko-KR');

let chart;                 // 공용 Chart.js 인스턴스
let currentTab = 'members';
let aborter;               // 현재 탭 요청 취소용

function buildQuery() {
    const from = $('#from')?.value;
    const to   = $('#to')?.value;
    const p = new URLSearchParams();
    if (from) p.set('from', from);
    if (to)   p.set('to', to);
    p.set('_ts', Date.now()); // 캐시 억제
    const q = p.toString();
    return q ? `?${q}` : '';
}

function setPanelState(state) {
    $('#panel-loading').hidden = state !== 'loading';
    $('#panel-empty').hidden   = state !== 'empty';
    $('#panel-error').hidden   = state !== 'error';
    if (state === 'ok') {
        $('#panel-loading').hidden = true;
        $('#panel-empty').hidden   = true;
        $('#panel-error').hidden   = true;
    }
}

function fmtWon(v){ return `₩${num.format(Number(v||0))}`; }
function fmtInt(v){ return num.format(Number(v||0)); }
function pct(v){ return `${Number(v||0).toFixed(1)}%`; }

// ---------- 공용 차트 렌더 ----------
function renderChart(data, options) {
    const ctx = $('#mainChart').getContext('2d');

    // 타입 또는 스택 여부가 바뀌면 재생성
    const needRecreate =
        !chart ||
        chart.config.type !== options.type ||
        Boolean(options.stacked) !== Boolean(chart?.options?.scales?.x?.stacked);

    const baseOpts = {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: { legend: { position: 'top', labels: { usePointStyle: true } } },
        scales: { y: { beginAtZero: true } }
    };

    // y축 구성 덮어쓰기
    if (options.scales) baseOpts.scales = options.scales;

    if (options.stacked) {
        baseOpts.scales = {
            x: { stacked: true, grid: { display: false } },
            y: { stacked: true, beginAtZero: true }
        };
    }

    if (needRecreate) {
        if (chart) chart.destroy();
        chart = new Chart(ctx, { type: options.type, data, options: baseOpts });
    } else {
        chart.data.labels   = data.labels;
        chart.data.datasets = data.datasets;
        chart.update();
    }
}

// ---------- 표 렌더(랭킹 탭에서만) ----------
function renderSideTable(columns, rows, rowTpl) {
    const card  = $('#side-table-card');
    const thead = $('#sideThead');
    const tbody = $('#sideTbody');
    const empty = $('#side-empty');

    if (!rows || rows.length === 0) {
        card.hidden = false;
        thead.innerHTML = `<tr>${columns.map(c=>`<th class="${c.cls||''}">${c.label}</th>`).join('')}</tr>`;
        tbody.innerHTML = '';
        empty.hidden = false;
        return;
    }

    empty.hidden = true;
    card.hidden = false;
    thead.innerHTML = `<tr>${columns.map(c=>`<th class="${c.cls||''}">${c.label}</th>`).join('')}</tr>`;
    tbody.innerHTML = rows.map(rowTpl).join('');
}

// ---------- 각 탭 로더 ----------
async function loadMembers(q) {
    $('#panel-title').textContent = '회원 성장 추이 (월별 가입자 수)';
    $('#side-table-card').hidden = true;

    const qs = new URLSearchParams(q.slice(1));         // 'from','to'
    const ym  = (s) => (s || '').slice(0, 7);           // 'yyyy-MM'
    const url = `/admin/chart/api/signups/monthly?from=${ym(qs.get('from'))}&to=${ym(qs.get('to'))}&_ts=${Date.now()}`;
    setPanelState('loading');
    try {
        const res = await fetch(url, { signal: aborter.signal, cache: 'no-store', headers:{Accept:'application/json'} });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const rows = await res.json(); // [{ym,newUsers}] or [{month,count}]
        const labels = rows.map(r => r.ym ?? r.month);
        const counts = rows.map(r => (r.newUsers ?? r.count) ?? 0);

        if (labels.length === 0) {
            setPanelState('empty');
            renderChart({labels:[],datasets:[]}, {type:'line'});
            return;
        }

        setPanelState('ok');
        renderChart({
            labels,
            datasets: [{ label:'월별 가입자 수', data: counts, tension:.25, pointRadius:3, fill:false }]
        }, {
            type: 'line',
            scales: { y: { beginAtZero: true, ticks: { precision: 0 }, title:{display:true,text:'명'} } }
        });
    } catch (e) {
        if (e.name === 'AbortError') return;
        console.error(e);
        setPanelState('error');
    }
}

async function loadRevenue(q) {
    $('#panel-title').textContent = '매출/결제 현황 (월별)';
    $('#side-table-card').hidden = true;

    const url = `/admin/chart/api/revenue/monthly${q}`;
    setPanelState('loading');
    try {
        const res = await fetch(url, { signal: aborter.signal, cache: 'no-store', headers:{Accept:'application/json'} });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const rows = await res.json(); // [{ym,revenue,paidCount,failOrCancelCount}]
        const labels    = rows.map(r => r.ym);
        const revenues  = rows.map(r => Number(r.revenue||0));
        const paid      = rows.map(r => Number(r.paidCount||0));
        const failed    = rows.map(r => Number(r.failOrCancelCount||0));

        if (labels.length === 0) {
            setPanelState('empty');
            renderChart({labels:[],datasets:[]}, {type:'bar'});
            return;
        }

        setPanelState('ok');
        renderChart({
            labels,
            datasets: [
                { type:'bar',  label:'매출액(원)', data: revenues, yAxisID:'y1' },
                { type:'line', label:'결제 건수',   data: paid,     yAxisID:'y2', tension:.3, pointRadius:3 },
                { type:'line', label:'실패/취소 건수', data: failed, yAxisID:'y2', tension:.3, pointRadius:3 }
            ]
        }, {
            type: 'bar',
            scales: {
                y1: { beginAtZero:true, position:'left',  title:{display:true,text:'매출액(원)'} },
                y2: { beginAtZero:true, position:'right', title:{display:true,text:'건수'}, grid:{ drawOnChartArea:false } }
            }
        });
    } catch (e) {
        if (e.name === 'AbortError') return;
        console.error(e);
        setPanelState('error');
    }
}

async function loadInstructors(q) {
    $('#panel-title').textContent = '강사별 강의 운영 현황 (Top5)';
    $('#side-title').textContent  = '강사 Top5 (상세)';

    const url = `/admin/chart/api/instructors/ops${q}`;
    setPanelState('loading');
    try {
        const res = await fetch(url, { signal: aborter.signal, cache:'no-store', headers:{Accept:'application/json'} });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const rows = await res.json(); // [{tutorName,confirmed,pending,canceled,revenue,fillRate}]
        const labels    = rows.map(r => r.tutorName ?? '(미등록)');
        const confirmed = rows.map(r => Number(r.confirmed||0));
        const pending   = rows.map(r => Number(r.pending||0));
        const canceled  = rows.map(r => Number(r.canceled||0));
        const revenue   = rows.map(r => Number(r.revenue||0));
        const ratePct   = rows.map(r => Math.round(((r.fillRate||0)*100)*10)/10);

        if (labels.length === 0) {
            setPanelState('empty');
            renderChart({labels:[],datasets:[]}, {type:'bar', stacked:true});
            renderSideTable(
                [
                    {label:'강사'},
                    {label:'확정', cls:'text-end text-success'},
                    {label:'대기', cls:'text-end text-warning'},
                    {label:'취소', cls:'text-end text-danger'},
                    {label:'매출', cls:'text-end'},
                    {label:'확정률', cls:'text-end'}
                ],
                [],
                ()=>''
            );
            return;
        }

        setPanelState('ok');

        // 차트
        renderChart({
            labels,
            datasets: [
                { type:'bar',  label:'확정',       data: confirmed, stack:'s' },
                { type:'bar',  label:'대기',       data: pending,   stack:'s' },
                { type:'bar',  label:'취소',       data: canceled,  stack:'s' },
                { type:'line', label:'매출(원)',    data: revenue,   yAxisID:'y2', tension:.3, pointRadius:3 },
                { type:'line', label:'확정률(%)',   data: ratePct,   yAxisID:'y3', tension:.3, pointRadius:3 }
            ]
        }, {
            type: 'bar',
            stacked: true,
            scales: {
                y:  { stacked:true, beginAtZero:true, title:{display:true,text:'건수'} },
                y2: { position:'right', beginAtZero:true, title:{display:true,text:'매출(원)'}, grid:{ drawOnChartArea:false } },
                y3: { position:'right', beginAtZero:true, title:{display:true,text:'확정률(%)'}, grid:{ drawOnChartArea:false },
                    ticks:{ callback:v => `${v}%` } }
            }
        });

        // 표
        $('#side-table-card').hidden = false;
        renderSideTable(
            [
                {label:'강사'},
                {label:'확정', cls:'text-end text-success'},
                {label:'대기', cls:'text-end text-warning'},
                {label:'취소', cls:'text-end text-danger'},
                {label:'매출', cls:'text-end'},
                {label:'확정률', cls:'text-end'}
            ],
            rows,
            r => `
        <tr>
          <td>${r.tutorName ?? '(미등록)'}</td>
          <td class="text-end text-success">${fmtInt(r.confirmed)}</td>
          <td class="text-end text-warning">${fmtInt(r.pending)}</td>
          <td class="text-end text-danger">${fmtInt(r.canceled)}</td>
          <td class="text-end">${fmtWon(r.revenue)}</td>
          <td class="text-end">${pct((r.fillRate||0)*100)}</td>
        </tr>`
        );

    } catch (e) {
        if (e.name === 'AbortError') return;
        console.error(e);
        setPanelState('error');
    }
}

async function loadLectures(q) {
    $('#panel-title').textContent = '인기 강좌 Top5';
    $('#side-title').textContent  = '인기 강좌(Top5)';

    const url = `/admin/chart/api/lectures/top${q}`;
    setPanelState('loading');
    try {
        const res = await fetch(url, { signal: aborter.signal, cache:'no-store', headers:{Accept:'application/json'} });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        let rows = await res.json(); // [{title,confirmed,canceled,pending,total,reservationRate}]
        // 정렬(확정 desc → total desc)
        rows.sort((a,b)=> (b.confirmed??0)-(a.confirmed??0) || (b.total??0)-(a.total??0));

        const labels    = rows.map(r => r.title ?? `강좌 ${r.lectureId}`);
        const confirmed = rows.map(r => Number(r.confirmed||0));
        const canceled  = rows.map(r => Number(r.canceled||0));
        const pending   = rows.map(r => Number(r.pending||0));
        const ratePct   = rows.map(r => {
            const v = r.reservationRate ?? 0;
            const pct = v > 1 ? v : v*100;
            return Math.round(pct*10)/10;
        });

        if (labels.length === 0) {
            setPanelState('empty');
            renderChart({labels:[],datasets:[]}, {type:'bar', stacked:true});
            renderSideTable(
                [
                    {label:'강좌'},
                    {label:'전체', cls:'text-end'},
                    {label:'확정', cls:'text-end text-success'},
                    {label:'취소', cls:'text-end text-danger'},
                    {label:'대기', cls:'text-end text-warning'},
                    {label:'예약률', cls:'text-end'}
                ],
                [],
                ()=>''
            );
            return;
        }

        setPanelState('ok');

        // 차트
        renderChart({
            labels,
            datasets: [
                { type:'bar',  label:'확정',     data: confirmed, stack:'s' },
                { type:'bar',  label:'취소',     data: canceled,  stack:'s' },
                { type:'bar',  label:'대기',     data: pending,   stack:'s' },
                { type:'line', label:'예약률(%)', data: ratePct,   yAxisID:'y2', tension:.35, pointRadius:3 }
            ]
        }, {
            type: 'bar',
            stacked: true,
            scales: {
                x: { stacked:true, grid:{ display:false } },
                y: { stacked:true, beginAtZero:true, title:{display:true,text:'건수'} },
                y2:{ position:'right', beginAtZero:true, suggestedMax:100, title:{display:true,text:'예약률(%)'},
                    grid:{ drawOnChartArea:false }, ticks:{ callback:v=>`${v}%` } }
            }
        });

        // 표
        $('#side-table-card').hidden = false;
        renderSideTable(
            [
                {label:'강좌'},
                {label:'전체', cls:'text-end'},
                {label:'확정', cls:'text-end text-success'},
                {label:'취소', cls:'text-end text-danger'},
                {label:'대기', cls:'text-end text-warning'},
                {label:'예약률', cls:'text-end'}
            ],
            rows,
            r => {
                const rate = (r.reservationRate!=null)
                    ? ((r.reservationRate>1? r.reservationRate : r.reservationRate*100).toFixed(1)+'%')
                    : '-';
                return `
          <tr>
            <td title="${r.title ?? ''}">${r.title ?? `강좌 ${r.lectureId}`}</td>
            <td class="text-end">${fmtInt(r.total)}</td>
            <td class="text-end text-success">${fmtInt(r.confirmed)}</td>
            <td class="text-end text-danger">${fmtInt(r.canceled)}</td>
            <td class="text-end text-warning">${fmtInt(r.pending)}</td>
            <td class="text-end">${rate}</td>
          </tr>`;
            }
        );

    } catch (e) {
        if (e.name === 'AbortError') return;
        console.error(e);
        setPanelState('error');
    }
}

// ---------- 탭 디스패처 ----------
async function loadCurrent() {
    if (aborter) aborter.abort();
    aborter = new AbortController();

    // 표 초기화/숨김
    $('#side-empty').hidden = true;
    $('#side-table-card').hidden = !(currentTab==='instructors' || currentTab==='lectures');
    $('#sideThead').innerHTML = '';
    $('#sideTbody').innerHTML = '';

    const q = buildQuery();

    if (currentTab === 'members')      return loadMembers(q);
    if (currentTab === 'revenue')      return loadRevenue(q);
    if (currentTab === 'instructors')  return loadInstructors(q);
    return loadLectures(q); // 'lectures'
}

// ---------- 초기화 ----------
document.addEventListener('DOMContentLoaded', () => {
    // 기본 기간: 올해 1월 1일 ~ 오늘
    const now = new Date();
    const to   = now.toISOString().slice(0,10);
    const from = new Date(now.getFullYear(), 0, 1).toISOString().slice(0,10);
    $('#from').value ||= from;
    $('#to').value   ||= to;

    // 탭 클릭
    $$('.lv-tab').forEach(btn => {
        btn.addEventListener('click', () => {
            if (btn.classList.contains('is-active')) return;
            $$('.lv-tab').forEach(b => b.classList.remove('is-active'));
            btn.classList.add('is-active');
            currentTab = btn.dataset.tab;
            loadCurrent();
        });
    });

    // 필터 버튼
    $('#applyFilter')?.addEventListener('click', loadCurrent);
    // (선택) 상단 #load 버튼도 동일 동작으로 묶기
    $('#load')?.addEventListener('click', () => $('#applyFilter')?.click());

    // 첫 로드
    loadCurrent();
});

// ---------- 브로드캐스트/스토리지 동기화(선택) ----------
try {
    const bc = new BroadcastChannel('reservations');
    bc.onmessage = (e) => {
        if (e?.data?.type === 'changed') loadCurrent();
    };
    window.addEventListener('beforeunload', () => bc.close());
} catch(_) {}

window.addEventListener('storage', (e) => {
    if (e.key === 'reservationChanged') loadCurrent();
});
