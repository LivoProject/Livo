// 유틸
const $  = (s, p=document) => p.querySelector(s);
const $$ = (s, p=document) => [...p.querySelectorAll(s)];
const fmt = new Intl.NumberFormat('ko-KR');

let aborter;           // 중복 요청 방지
let topChartInstance;  // 차트 핸들

function show(el){ el.hidden = false; }
function hide(el){ el.hidden = true; }

function setLoading(is) {
    const loading = $("#chart-loading");
    is ? show(loading) : hide(loading);
}
function setError(is)   { (is ? show : hide)($("#chart-error")); }
function setEmpty(is)   { (is ? show : hide)($("#chart-empty")); (is ? show : hide)($("#table-empty")); }

// 차트 생성: 상태별 스택 막대 + 예약률(라인)
function renderTopChart(rows) {
    const canvas = $("#topLectures");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (topChartInstance) topChartInstance.destroy();

    const labels    = rows.map(r => r.title ?? `강좌 ${r.lectureId}`);
    const confirmed = rows.map(r => r.confirmed ?? 0);
    const canceled  = rows.map(r => r.canceled  ?? 0);
    const pending   = rows.map(r => r.pending   ?? 0);
    const ratePct = rows.map(r => {
        const v = r.reservationRate ?? 0;        // 서버 값(0~1 또는 0~100)
        const pct = v > 1 ? v : v * 100;
        const safe = Number.isFinite(pct) ? pct : 0;  // 0~1이면 ×100, 이미 %면 그대로
        return Math.round(safe * 10) / 10;       // 소수 1자리
    });


    topChartInstance = new Chart(ctx, {
        data: {
            labels,
            datasets: [
                { type:'bar', label:'확정', data: confirmed, stack:'s',
                    backgroundColor:'rgba(59,130,246,.7)' },        // 파랑
                { type:'bar', label:'취소', data: canceled,  stack:'s',
                    backgroundColor:'rgba(244,63,94,.25)' },        // 연분홍
                { type:'bar', label:'대기', data: pending,   stack:'s',
                    backgroundColor:'rgba(245,158,11,.25)' },       // 연주황
                { type:'line', label:'예약률(%)', data: ratePct, yAxisID:'y2',
                    borderColor:'rgba(250,204,21,1)',
                    pointBackgroundColor:'rgba(250,204,21,1)',
                    tension:.35, pointRadius:3, borderWidth:2 }
            ]
        },
        options: {
            responsive:true, maintainAspectRatio:false,
            layout:{ padding:{left:8,right:16,top:8,bottom:0} },
            interaction:{ mode:'index', intersect:false },
            elements:{ bar:{ borderRadius:6, maxBarThickness:28 } },
            scales:{
                x:{ stacked:true, grid:{ display:false }, ticks:{ maxRotation:0, autoSkip:true } },
                y:{ stacked:true, beginAtZero:true, grace:'10%', ticks:{ stepSize:1 }, title:{display:true, text:'건수'} },
                y2:{ beginAtZero:true, suggestedMax:100, position:'right',
                    grid:{ drawOnChartArea:false }, ticks:{ callback:v=>`${v}%` },
                    title:{ display:true, text:'예약률(%)' } }
            },
            plugins:{
                legend:{ position:'top', labels:{ usePointStyle:true } },
                tooltip:{
                    padding:10,
                    callbacks:{
                        // 퍼센트를 천단위 포맷(fmt)하지 않도록 분기
                        label:(ctx)=>{
                            const v = ctx.parsed.y ?? ctx.raw;
                            const isRate = ctx.dataset.yAxisID==='y2';
                            return ` ${ctx.dataset.label}: ${isRate ? (Number(v).toFixed(1)+'%') : fmt.format(v)}`;
                        }
                    }
                }
            }
        }
    });
}

// 표 렌더링
function renderTable(rows) {
    const tb = $("#topTable tbody");
    tb.innerHTML = rows.map(r => {
        const v = r.reservationRate ?? 0;
        const pct = v > 1 ? v : v * 100;
        const rate = (r.reservationRate != null)
            ? ((v > 1 ? v : v * 100).toFixed(1) + '%')
            : '-';
        const title = r.title ?? `강좌 ${r.lectureId}`;
        return `
      <tr>
        <td title="${title}">${title}</td>
        <td class="text-end">${fmt.format(r.total ?? 0)}</td>
        <td class="text-end text-success">${fmt.format(r.confirmed ?? 0)}</td>
        <td class="text-end text-danger">${fmt.format(r.canceled ?? 0)}</td>
        <td class="text-end text-warning">${fmt.format(r.pending ?? 0)}</td>
        <td class="text-end">${rate}</td>
      </tr>`;
    }).join("");
}

// 데이터 로딩
async function loadTopLectures(from, to) {
    if (aborter) aborter.abort();
    aborter = new AbortController();

    setError(false); setEmpty(false); setLoading(true);

    try {
        const url = new URL("/admin/chart/api/lectures/top", location.origin);
        url.searchParams.set("from", from);
        url.searchParams.set("to", to);
        url.searchParams.set("limit", 5);
        url.searchParams.set("_ts", Date.now());

        const res = await fetch(url, { signal: aborter.signal, cache: 'no-store' });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();

        if (!data || data.length === 0) {
            setEmpty(true);
            if (topChartInstance) topChartInstance.destroy();
            renderTable([]);
            return;
        }

        data.sort((a,b) => (b.confirmed??0)-(a.confirmed??0) || (b.total??0)-(a.total??0));
        renderTopChart(data);
        renderTable(data);
    } catch (e) {
        if (e.name !== 'AbortError') {
            console.error(e);
            setError(true);
        }
    } finally {
        setLoading(false);
    }
}


// 초기화
document.addEventListener("DOMContentLoaded", () => {
    const now = new Date();
    const to = now.toISOString().slice(0,10);
    const from = new Date(now.getFullYear(), 0, 1).toISOString().slice(0,10);


    $("#from").value = from;
    $("#to").value   = to;

    $("#from").addEventListener("change", () => loadTopLectures($("#from").value, $("#to").value));
    $("#to").addEventListener("change",   () => loadTopLectures($("#from").value, $("#to").value));
    $("#load").addEventListener("click",  () => loadTopLectures($("#from").value, $("#to").value));

    // 버튼 클릭만 요청 (자동 반복 방지)
    $("#load").addEventListener("click", () => {
        loadTopLectures($("#from").value, $("#to").value);
    });

    // 최초 1회
    loadTopLectures(from, to);
});
// === 다른 탭/페이지(예: 마이페이지)에서 취소 시 차트 자동 갱신 === //
try {
    const bc = new BroadcastChannel('reservations');
    bc.onmessage = (e) => {
        if (e?.data?.type === 'changed') {
            const from = $("#from")?.value, to = $("#to")?.value;
            if (from && to) loadTopLectures(from, to);
        }
    };
    window.addEventListener('beforeunload', () => bc.close());
} catch(_) {}


// 로컬스토리지 이벤트(브라우저 호환용)
window.addEventListener("storage", (e) => {
    if (e.key === "reservationChanged") {
        const from = $("#from")?.value;
        const to = $("#to")?.value;
        if (from && to) loadTopLectures(from, to);
    }
});

