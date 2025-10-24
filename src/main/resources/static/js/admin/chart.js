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
    const ctx = $("#topLectures").getContext("2d");
    if (topChartInstance) topChartInstance.destroy();

    const labels    = rows.map(r => r.title ?? `강좌 ${r.lectureId}`);
    const confirmed = rows.map(r => r.confirmed ?? 0);
    const canceled  = rows.map(r => r.canceled  ?? 0);
    const pending   = rows.map(r => r.pending   ?? 0);
    const ratePct   = rows.map(r => Math.round((r.reservationRate ?? 0) * 1000) / 10); // 0~100 한자리

    topChartInstance = new Chart(ctx, {
        data: {
            labels,
            datasets: [
                { type:'bar', label:'확정', data: confirmed, stack:'s' },
                { type:'bar', label:'취소', data: canceled,  stack:'s' },
                { type:'bar', label:'대기', data: pending,   stack:'s' },
                { type:'line', label:'예약률(%)', data: ratePct, yAxisID:'y2', tension:.35, pointRadius:3, borderWidth:2 }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode:'index', intersect:false },
            scales: {
                x: { stacked:true },
                y: { stacked:true, beginAtZero:true, title:{display:true, text:'건수'} },
                y2:{ beginAtZero:true, max:100, position:'right', grid:{ drawOnChartArea:false },
                    title:{display:true, text:'예약률(%)'} }
            },
            plugins: {
                legend: { position:'top' },
                tooltip: {
                    callbacks: {
                        label: (ctx) => {
                            const v = ctx.parsed.y ?? ctx.raw;
                            return `${ctx.dataset.label}: ${fmt.format(v)}${ctx.dataset.yAxisID==='y2' ? '%' : ''}`;
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
        const rate = (r.reservationRate!=null) ? ((r.reservationRate*100).toFixed(1)+'%') : '-';
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
    // 이전 요청 중단
    if (aborter) aborter.abort();
    aborter = new AbortController();

    setError(false); setEmpty(false); setLoading(true);

    try {
        const url = new URL("/admin/chart/api/lectures/top", location.origin);
        url.searchParams.set("from", from);
        url.searchParams.set("to", to);
        url.searchParams.set("limit", 5);
        url.searchParams.set("_ts", Date.now()); // 매번 다른 요청으로 인식

        const res = await fetch(url, { signal: aborter.signal, cache: 'no-store' });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();

        setLoading(false);

        if (!data || data.length === 0) {
            setEmpty(true);
            if (topChartInstance) topChartInstance.destroy();
            renderTable([]);
            return;
        }

        // 실무 감성: 기본은 확정 내림차순 → 동일 시 전체 내림차순
        data.sort((a,b) => (b.confirmed??0)-(a.confirmed??0) || (b.total??0)-(a.total??0));

        renderTopChart(data);
        renderTable(data);
    } catch (e) {
        if (e.name === 'AbortError') return; // 새 요청으로 취소된 케이스
        console.error(e);
        setLoading(false);
        setError(true);
    }
}

// 초기화
document.addEventListener("DOMContentLoaded", () => {
    const now = new Date();
    const to = now.toISOString().slice(0,10);
    const from = new Date(now.getFullYear(), 0, 1).toISOString().slice(0,10);

    $("#from").value = from;
    $("#to").value = to;

    // 버튼 클릭만 요청 (자동 반복 방지)
    $("#load").addEventListener("click", () => {
        loadTopLectures($("#from").value, $("#to").value);
    });

    // 최초 1회
    loadTopLectures(from, to);
});
// === 다른 탭/페이지(예: 마이페이지)에서 취소 시 차트 자동 갱신 === //
try {
    const bc = new BroadcastChannel("reservations");
    bc.onmessage = (e) => {
        if (e?.data?.type === "changed") {
            const from = $("#from")?.value;
            const to = $("#to")?.value;
            if (from && to) loadTopLectures(from, to);
        }
    };
} catch (_) {}

// 로컬스토리지 이벤트(브라우저 호환용)
window.addEventListener("storage", (e) => {
    if (e.key === "reservationChanged") {
        const from = $("#from")?.value;
        const to = $("#to")?.value;
        if (from && to) loadTopLectures(from, to);
    }
});

