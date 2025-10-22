function loadReports(page = 0) {
    $.ajax({
        url: `/admin/report/list`,
        type: 'GET',
        data: { page: page, pageSize: 10 },
        success: function (pageData) {
            const tbody = $("#reportTable tbody");
            tbody.empty();

            // 데이터 반복 출력
            pageData.content.forEach((r, idx) => {
                tbody.append(`
                    <tr class="text-center">
                        <td>${page * 10 + idx + 1}</td>
                        <td>${r.email}</td>
                        <td>${r.reportReason}</td>
                        <td>${r.status}</td>
                        <td>${r.reportTime ? r.reportTime.replace('T',' ') : ''}</td>
                    </tr>
                `);
            });

            renderPagination(pageData);
        },
        error: function (err) {
            console.error(err);
            showCommonModal("오류", "신고 리스트를 불러오는 중 문제가 발생했습니다.");
        }
    });
}
function renderPagination(pageData) {
    const pagination = $("#pagination");
    pagination.empty();

    for (let i = 0; i < pageData.totalPages; i++) {
        const active = i === pageData.number ? "active" : "";
        pagination.append(`
            <li class="page-item ${active}">
                <a class="page-link" href="#" onclick="loadReports(${i})">${i + 1}</a>
            </li>
        `);
        li.click(function(e) {
            e.preventDefault(); // 👈 스크롤 튀는 것 방지
            loadReports(i);
        });
        pagination.append(li);
    }
}
