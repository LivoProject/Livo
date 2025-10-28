document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ dashboard-chart.js loaded");
    const today = new Date().toISOString().slice(0,10);
    const yearStart = new Date(new Date().getFullYear(), 0, 1).toISOString().slice(0,10);
    const fmt = new Intl.NumberFormat('ko-KR');
    const charts = {};
    const loading = {}; // 중복 호출 방지용
    // ====================== 공통 렌더 함수 ======================
    async function renderChartSafe(id, renderChart) {
        if (loading[id]) return;       // 이미 로딩 중이면 무시
        loading[id] = true;

        try {
            if (charts[id]) {
                charts[id].destroy();
                charts[id] = null;
            }
            await renderChart();          // 실제 렌더 함수 실행
        } finally {
            loading[id] = false;
        }
    }
    async function renderChart(canvasId, type, datasets, labels, options = {}) {
        const canvas = document.getElementById(canvasId);
        if (!canvas) return;

        //  기존 차트 파괴 및 canvas 완전 초기화
        if (charts[canvasId]) {
            charts[canvasId].destroy();
            charts[canvasId] = null;

            // canvas 완전 교체 (Chart.js 내부 ctx 참조 초기화)
            const newCanvas = canvas.cloneNode(true);
            canvas.parentNode.replaceChild(newCanvas, canvas);
        }

        const ctx = document.getElementById(canvasId)?.getContext("2d");
        if (!ctx) return;

        charts[canvasId] = new Chart(ctx, {
            type,
            data: { labels, datasets },
            options: Object.assign({
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: "top" } },
                scales: { y: { beginAtZero: true } }
            }, options)
        });
    }


    // ====================== ① 인기 강좌 Top5 ======================
    async function loadTopLectures() {
        const res = await fetch(`/admin/chart/api/lectures/top?from=${yearStart}&to=${today}&limit=5`);
        const data = await res.json();
        const labels = data.map(r => r.title ?? `강좌 ${r.lectureId}`);
        const confirmed = data.map(r => r.confirmed ?? 0);
        const canceled  = data.map(r => r.canceled ?? 0);
        const pending   = data.map(r => r.pending ?? 0);
        const ratePct   = data.map(r => Math.round(((r.reservationRate ?? 0) * 100) * 10)/10);

        await renderChart("chart1", "bar", [
            { type:'bar', label:'확정', data: confirmed, stack:'s', backgroundColor:'rgba(59,130,246,.7)' },
            { type:'bar', label:'취소', data: canceled,  stack:'s', backgroundColor:'rgba(244,63,94,.25)' },
            { type:'bar', label:'대기', data: pending,   stack:'s', backgroundColor:'rgba(245,158,11,.25)' },
            { type:'line', label:'예약률(%)', data: ratePct, yAxisID:'y2',
                borderColor:'rgba(250,204,21,1)', pointBackgroundColor:'rgba(250,204,21,1)', tension:.35, pointRadius:3, borderWidth:2 }
        ], labels, {
            scales:{
                x:{ stacked:true, grid:{ display:false } },
                y:{ stacked:true, beginAtZero:true, title:{ display:true, text:'건수' }},
                y2:{ beginAtZero:true, position:'right', grid:{drawOnChartArea:false},
                    title:{display:true,text:'예약률(%)'}, ticks:{callback:v=>`${v}%`}}
            }
        });
    }

    // ====================== ② 월별 매출 ======================
    async function loadMonthlyRevenue() {
        const res = await fetch(`/admin/chart/api/revenue/monthly?from=${yearStart}&to=${today}`);
        const data = await res.json();
        const labels = data.map(r => r.ym ?? r.month);
        const revenues = data.map(r => Number(r.revenue ?? 0));
        const paidCounts = data.map(r => r.paidCount ?? 0);
        const failCounts = data.map(r => r.failOrCancelCount ?? 0);

        await renderChart("chart2", "bar", [
            { type:'bar', label:'매출액(원)', data: revenues, yAxisID:'y1', backgroundColor:'rgba(59,130,246,0.6)' },
            { type:'line', label:'결제 건수', data: paidCounts, yAxisID:'y2', borderColor:'rgba(34,197,94,1)', tension:0.3 },
            { type:'line', label:'실패/취소 건수', data: failCounts, yAxisID:'y2', borderColor:'rgba(239,68,68,1)', tension:0.3 }
        ], labels, {
            scales:{
                y1:{ beginAtZero:true, position:'left', title:{display:true,text:'매출(원)'} },
                y2:{ beginAtZero:true, position:'right', grid:{drawOnChartArea:false}, title:{display:true,text:'건수'} }
            },
            plugins:{ tooltip:{
                    callbacks:{
                        label:(ctx)=>{
                            const v = ctx.parsed.y ?? 0;
                            if (ctx.dataset.label.includes("매출")) return ` ${ctx.dataset.label}: ₩${fmt.format(v)}`;
                            return ` ${ctx.dataset.label}: ${fmt.format(v)}건`;
                        }
                    }
                }}
        });
    }

    // ====================== ③ 강사별 운영 현황 ======================
    async function loadInstructorOps() {
        const res = await fetch(`/admin/chart/api/instructors/ops?from=${yearStart}&to=${today}&limit=5`);
        const data = await res.json();
        const labels = data.map(r => r.tutorName ?? '(미등록)');
        const confirmed = data.map(r => r.confirmed ?? 0);
        const pending   = data.map(r => r.pending ?? 0);
        const canceled  = data.map(r => r.canceled ?? 0);
        const revenue   = data.map(r => Number(r.revenue ?? 0));
        const fillRate  = data.map(r => Math.round(((r.fillRate ?? 0) * 100) * 10)/10);

        await renderChart("chart3", "bar", [
            { type:'bar', label:'확정', data: confirmed, stack:'s', backgroundColor:'rgba(34,197,94,.7)' },
            { type:'bar', label:'대기', data: pending,   stack:'s', backgroundColor:'rgba(245,158,11,.35)' },
            { type:'bar', label:'취소', data: canceled,  stack:'s', backgroundColor:'rgba(239,68,68,.25)' },
            { type:'line',label:'매출(원)', data: revenue, yAxisID:'y2', borderColor:'rgba(59,130,246,1)', tension:.3, pointRadius:3 },
            { type:'line',label:'확정률(%)', data: fillRate, yAxisID:'y3', borderColor:'rgba(250,204,21,1)', tension:.3, pointRadius:3 }
        ], labels, {
            scales:{
                x:{ stacked:true, grid:{ display:false }},
                y:{ stacked:true, beginAtZero:true, title:{ display:true, text:'건수' }},
                y2:{ position:'right', beginAtZero:true, grid:{ drawOnChartArea:false }, title:{ display:true, text:'매출(원)' }},
                y3:{ position:'right', beginAtZero:true, grid:{ drawOnChartArea:false }, ticks:{ callback:v=>`${v}%` }, title:{ display:true, text:'확정률(%)' }}
            }
        });
    }

    // ====================== ④ 월별 가입자 수 ======================
    async function loadMonthlySignups() {
        const res = await fetch(`/admin/chart/api/signups/monthly?from=2025-01&to=2025-10`);
        const data = await res.json();
        const labels = data.map(r => (r.ym ?? r.month));
        const counts = data.map(r => ((r.newUsers ?? r.count) ?? 0));

        await renderChart("chart4", "line", [{
            label: '월별 가입자 수',
            data: counts,
            borderColor: 'rgba(59,130,246,1)',
            tension: .25,
            pointRadius: 3,
            fill: false
        }], labels);
    }

    // ====================== 탭 전환 시 로드 ======================
    const tabTriggers = document.querySelectorAll('[data-bs-toggle="tab"], [data-bs-toggle="pill"]');
    tabTriggers.forEach(tab => {
        tab.addEventListener("shown.bs.tab", (e) => {
            const targetId = e.target.getAttribute("data-bs-target");
            console.log("탭 전환됨:", targetId); //
            setTimeout(() => {
                if (targetId === "#tab1") renderChartSafe("chart1", loadTopLectures);
                if (targetId === "#tab2") renderChartSafe("chart2", loadMonthlyRevenue);
                if (targetId === "#tab3") renderChartSafe("chart3", loadInstructorOps);
                if (targetId === "#tab4") renderChartSafe("chart4", loadMonthlySignups);
            },200);
        });
    });


    // 첫 번째 탭 기본 로드
    loadTopLectures();
});
