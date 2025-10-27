// =======================
// 유틸 및 전역 변수
// =======================
const $  = (s, p=document) => p.querySelector(s);
const $$ = (s, p=document) => [...p.querySelectorAll(s)];
const fmt = new Intl.NumberFormat('ko-KR');

let aborter;
let topChartInstance;

let signupChartInstance;
let signupAborter;

let revenueChartInstance;
let revenueAborter;

let instructorChartInstance;
let instructorAborter;

function show(el){ el.hidden = false; }
function hide(el){ el.hidden = true; }

function setLoading(is) {
    const loading = $("#chart-loading");
    is ? show(loading) : hide(loading);
}
function setError(is)   { (is ? show : hide)($("#chart-error")); }
function setEmpty(is)   { (is ? show : hide)($("#chart-empty")); (is ? show : hide)($("#table-empty")); }

// 날짜 → 연월 변환 유틸
function toYearMonth(dateStr){            // "yyyy-MM-dd" -> "yyyy-MM"
    return (dateStr || "").slice(0, 7);
}
function normalizeYmRange(fromYm, toYm){  // fromYm <= toYm 보장
    if (!fromYm || !toYm) return [fromYm, toYm];
    return fromYm > toYm ? [toYm, fromYm] : [fromYm, toYm];
}

// =======================
// 인기 강좌 Top5 차트
// =======================
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
        const v = r.reservationRate ?? 0;
        const pct = v > 1 ? v : v * 100;
        const safe = Number.isFinite(pct) ? pct : 0;
        return Math.round(safe * 10) / 10;
    });

    topChartInstance = new Chart(ctx, {
        data: {
            labels,
            datasets: [
                { type:'bar', label:'확정', data: confirmed, stack:'s',
                    backgroundColor:'rgba(59,130,246,.7)' },
                { type:'bar', label:'취소', data: canceled,  stack:'s',
                    backgroundColor:'rgba(244,63,94,.25)' },
                { type:'bar', label:'대기', data: pending,   stack:'s',
                    backgroundColor:'rgba(245,158,11,.25)' },
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

// =======================
// Top5 표 렌더링
// =======================
function renderTable(rows) {
    const tb = $("#topTable tbody");
    tb.innerHTML = rows.map(r => {
        const v = r.reservationRate ?? 0;
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

// =======================
// 회원 성장 추이 (월별 가입자 수)
// =======================
async function fetchMonthlySignups(fromYm, toYm){
    const $empty   = $("#signup-empty");
    const $loading = $("#signup-loading");
    const $error   = $("#signup-error");
    $empty.hidden = true; $error.hidden = true; $loading.hidden = false;

    if (signupAborter) signupAborter.abort();
    signupAborter = new AbortController();

    try {
        const url = new URL("/admin/chart/api/signups/monthly", location.origin);
        url.searchParams.set("from", fromYm);
        url.searchParams.set("to",   toYm);
        url.searchParams.set("_ts",  Date.now());

        const res = await fetch(url, {
            signal: signupAborter.signal,
            cache: 'no-store',
            headers: { Accept: "application/json" },
            credentials: 'include' // 세션 쿠키 포함(관리자 영역이면 권장)
        });

        if (!res.ok) {
            const txt = await res.text().catch(()=> "");
            console.error("[signups] HTTP", res.status, txt); // 🔎 서버 에러 메시지 확인
            throw new Error(`HTTP ${res.status}`);
        }
        const data = await res.json(); // [{ym, newUsers}]
        $loading.hidden = true;
        if (!data || data.length === 0) $empty.hidden = false;
        return data || [];
    } catch (e){
        if (e.name !== 'AbortError') {
            console.error(e);
            $loading.hidden = true;
            $error.hidden = false;
        }
        return [];
    }
}

async function fetchMonthlyRevenue(from, to) {
    const $empty   = $("#revenue-empty");
    const $loading = $("#revenue-loading");
    const $error   = $("#revenue-error");
    $empty.hidden = true; $error.hidden = true; $loading.hidden = false;

    if (revenueAborter) revenueAborter.abort();
    revenueAborter = new AbortController();

    try {
        const url = new URL("/admin/chart/api/revenue/monthly", location.origin);
        url.searchParams.set("from", from);
        url.searchParams.set("to", to);
        url.searchParams.set("_ts", Date.now());

        const res = await fetch(url, {
            signal: revenueAborter.signal,
            cache: "no-store",
            headers: { Accept: "application/json" },
            credentials: "include"
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        $loading.hidden = true;
        if (!data || data.length === 0) $empty.hidden = false;
        return data || [];
    } catch (e) {
        if (e.name !== "AbortError") {
            console.error(e);
            $loading.hidden = true;
            $error.hidden = false;
        }
        return [];
    }
}

function renderRevenueChart(rows) {
    const canvas = $("#revenueChart");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (revenueChartInstance) revenueChartInstance.destroy();

    const labels = rows.map(r => r.ym);
    const revenues = rows.map(r => Number(r.revenue ?? 0));
    const paidCounts = rows.map(r => r.paidCount);
    const failCounts = rows.map(r => r.failOrCancelCount);

    revenueChartInstance = new Chart(ctx, {
        data: {
            labels,
            datasets: [
                { type: "bar", label: "매출액(원)", data: revenues, yAxisID: "y1", backgroundColor: "rgba(59,130,246,0.6)" },
                { type: "line", label: "결제 건수", data: paidCounts, yAxisID: "y2", borderColor: "rgba(34,197,94,1)", tension: 0.3 },
                { type: "line", label: "실패/취소 건수", data: failCounts, yAxisID: "y2", borderColor: "rgba(239,68,68,1)", tension: 0.3 }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y1: {beginAtZero: true, position: "left", title: {display: true, text: "매출액(원)"}},
                y2: {
                    beginAtZero: true,
                    position: "right",
                    grid: {drawOnChartArea: false},
                    title: {display: true, text: "건수"}
                }
            },
            plugins: {
                legend: {position: "top"},
                tooltip: {
                    callbacks: {
                        label: (ctx) => {
                            const v = ctx.parsed.y ?? 0;
                            if (ctx.dataset.label?.includes("매출액")) {
                                return ` ${ctx.dataset.label}: ₩${fmt.format(v)}`;
                            }
                            return ` ${ctx.dataset.label}: ${fmt.format(v)}건`;
                        }
                    }
                }
            }
        }
    });
}

window.loadMonthlyRevenue = async function(from, to) {
    const data = await fetchMonthlyRevenue(from, to);
    renderRevenueChart(data);
};

async function fetchInstructorOps(from, to, limit=5){
    const $empty   = $("#instructor-empty");
    const $loading = $("#instructor-loading");
    const $error   = $("#instructor-error");
    $empty.hidden = true; $error.hidden = true; $loading.hidden = false;

    if (instructorAborter) instructorAborter.abort();
    instructorAborter = new AbortController();

    try{
        const url = new URL("/admin/chart/api/instructors/ops", location.origin);
        url.searchParams.set("from", from);
        url.searchParams.set("to", to);
        url.searchParams.set("limit", limit);
        url.searchParams.set("_ts", Date.now());

        const res = await fetch(url, { signal: instructorAborter.signal, cache:'no-store' });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();
        $loading.hidden = true;
        if (!data || data.length === 0) $empty.hidden = false;
        return data || [];
    }catch(e){
        if (e.name !== 'AbortError'){ console.error(e); $loading.hidden = true; $error.hidden = false; }
        return [];
    }
}

function renderInstructorChart(rows){
    const canvas = $("#instructorOpsChart");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (instructorChartInstance) instructorChartInstance.destroy();

    const labels    = rows.map(r => r.tutorName ?? '(미등록)');
    const confirmed = rows.map(r => r.confirmed ?? 0);
    const pending   = rows.map(r => r.pending   ?? 0);
    const canceled  = rows.map(r => r.canceled  ?? 0);
    const revenue   = rows.map(r => Number(r.revenue ?? 0));
    const fillRate  = rows.map(r => Math.round(((r.fillRate ?? 0) * 100) * 10)/10);

    instructorChartInstance = new Chart(ctx, {
        data: {
            labels,
            datasets: [
                { type:'bar', label:'확정', data: confirmed, stack:'s', backgroundColor:'rgba(34,197,94,.7)' },
                { type:'bar', label:'대기', data: pending,   stack:'s', backgroundColor:'rgba(245,158,11,.35)' },
                { type:'bar', label:'취소', data: canceled,  stack:'s', backgroundColor:'rgba(239,68,68,.25)' },
                { type:'line',label:'매출(원)', data: revenue, yAxisID:'y2',
                    borderColor:'rgba(59,130,246,1)', pointBackgroundColor:'rgba(59,130,246,1)', tension:.3, pointRadius:3 },
                { type:'line',label:'확정률(%)', data: fillRate, yAxisID:'y3',
                    borderColor:'rgba(250,204,21,1)', pointBackgroundColor:'rgba(250,204,21,1)', tension:.3, pointRadius:3 }
            ]
        },
        options:{
            responsive:true, maintainAspectRatio:false,
            interaction:{ mode:'index', intersect:false },
            elements:{ bar:{ borderRadius:6, maxBarThickness:28 }},
            scales:{
                x:{ stacked:true, grid:{ display:false }},
                y:{ stacked:true, beginAtZero:true, title:{ display:true, text:'건수' }},
                y2:{ position:'right', beginAtZero:true, grid:{ drawOnChartArea:false }, title:{ display:true, text:'매출(원)' }},
                y3:{ position:'right', beginAtZero:true, grid:{ drawOnChartArea:false }, ticks:{ callback:v=>`${v}%` }, title:{ display:true, text:'확정률(%)' }}
            },
            plugins:{
                legend:{ position:'top', labels:{ usePointStyle:true } },
                tooltip:{ callbacks:{
                        label:(ctx)=>{
                            const v = ctx.parsed.y ?? ctx.raw ?? 0;
                            if (ctx.dataset.label.includes('매출')) return ` ${ctx.dataset.label}: ₩${fmt.format(v)}`;
                            if (ctx.dataset.label.includes('확정률')) return ` ${ctx.dataset.label}: ${Number(v).toFixed(1)}%`;
                            return ` ${ctx.dataset.label}: ${fmt.format(v)}`;
                        }
                    }}
            }
        }
    });
}

window.loadInstructorOps = async function(from, to){
    const rows = await fetchInstructorOps(from, to, 5);
    renderInstructorChart(rows);
};



function renderSignupChart(rows){
    const canvas = $("#signupChart");
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (signupChartInstance) signupChartInstance.destroy();

     const labels = rows.map(r => (r.ym ?? r.month));           // 백엔드가 ym 사용
     const counts = rows.map(r => ((r.newUsers ?? r.count) ?? 0)); // 백엔드가 newUsers 사용

    signupChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: '월별 가입자 수',
                data: counts,
                tension: .25,
                pointRadius: 3,
                fill: false
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode:'nearest', intersect:false },
            plugins: {
                legend: { display: true },
                tooltip: {
                    padding: 10,
                    callbacks: {
                        label: (ctx) => ` ${ctx.dataset.label}: ${fmt.format(ctx.parsed.y ?? 0)}`
                    }
                }
            },
            scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
        }
    });
}
function renderInstructorTable(rows){
    const tb = $("#instructorTable tbody");
    if (!tb) return;

    if (!rows || rows.length === 0) {
        $("#instructor-table-empty")?.removeAttribute("hidden");
        tb.innerHTML = "";
        return;
    }
    $("#instructor-table-empty")?.setAttribute("hidden","hidden");

    tb.innerHTML = rows.map(r => {
        const name   = r.tutorName ?? '(미등록)';
        const conf   = r.confirmed ?? 0;
        const pend   = r.pending ?? 0;
        const canc   = r.canceled ?? 0;
        const rev    = Number(r.revenue ?? 0);
        const rate   = (((r.fillRate ?? 0) * 100).toFixed(1)) + '%';

        return `
      <tr title="${(r.tutorInfo ?? '').replace(/"/g,'&quot;')}">
        <td>${name}</td>
        <td class="text-end text-success">${fmt.format(conf)}</td>
        <td class="text-end text-warning">${fmt.format(pend)}</td>
        <td class="text-end text-danger">${fmt.format(canc)}</td>
        <td class="text-end">₩${fmt.format(rev)}</td>
        <td class="text-end">${rate}</td>
      </tr>`;
    }).join("");
}

// 외부에서 호출할 수 있도록 래퍼
window.loadMonthlySignups = async function(fromDate, toDate){
    const [fromYm, toYm] = normalizeYmRange(toYearMonth(fromDate), toYearMonth(toDate));
    if (!fromYm || !toYm) return;
    const data = await fetchMonthlySignups(fromYm, toYm);
    renderSignupChart(data);
};

// =======================
// 데이터 로딩: Top5
// =======================
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

// =======================
// 초기화
// =======================
document.addEventListener("DOMContentLoaded", () => {
    const now = new Date();
    const to = now.toISOString().slice(0,10);
    const from = new Date(now.getFullYear(), 0, 1).toISOString().slice(0,10);

    $("#from").value = from;
    $("#to").value   = to;

    // 한 번에 두 차트 모두 갱신
    const reloadAll = () => {
        const f = $("#from").value, t = $("#to").value;
        loadTopLectures(f, t);
        loadMonthlySignups(f, t);
        loadMonthlyRevenue(f, t);
        loadInstructorOps(f, t);
    };

    $("#from").addEventListener("change", reloadAll);
    $("#to").addEventListener("change",   reloadAll);
    $("#load").addEventListener("click",  reloadAll);

    // 최초 1회
    reloadAll();
});

// =======================
// 브로드캐스트 / 스토리지 동기화
// =======================
try {
    const bc = new BroadcastChannel('reservations');
    bc.onmessage = (e) => {
        if (e?.data?.type === 'changed') {
            const from = $("#from")?.value, to = $("#to")?.value;
            if (from && to) {
                loadTopLectures(from, to);
                loadMonthlySignups(from, to);
                loadMonthlyRevenue(from, to);
                loadInstructorOps(from, to);
            }
        }
    };
    window.addEventListener('beforeunload', () => bc.close());
} catch(_) {}

window.addEventListener("storage", (e) => {
    if (e.key === "reservationChanged") {
        const from = $("#from")?.value;
        const to = $("#to")?.value;
        if (from && to) {
            loadTopLectures(from, to);
            loadMonthlySignups(from, to);
            loadMonthlyRevenue(from, to);
            loadInstructorOps(from, to);
        }
    }
});
window.loadInstructorOps = async function(from, to){
    const rows = await fetchInstructorOps(from, to, 5);
    renderInstructorChart(rows);
    renderInstructorTable(rows); // ⬅️ 추가
};
