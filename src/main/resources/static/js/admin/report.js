$(document).ajaxSend(function (e, xhr, options) {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    if (token && header) xhr.setRequestHeader(header, token);
});
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
                        <td>
                            <div class="text-truncate-one" title="${r.reviewContent}" style="max-width: 100px">${r.reviewContent}</div>
                        </td>
                        <td>${r.reportReason}</td>
                        <td>${r.email}</td>
                        <td>${r.reportTime}</td>
                        <td>
                            <span class="badge ${r.status === 'PROCESSING' ? 'bg-warning' : r.status === 'COMPLETED' ? 'bg-success' : 'bg-danger'}">
                                ${r.status}
                            </span>
                        </td>
                        <td>
                            <button id="approveBtn" class="btn btn-sm btn-success update-status" data-id="${r.reportId}" data-status="COMPLETED">승인</button>
                            <button id="rejectBtn" class="btn btn-sm btn-danger update-status " data-id="${r.reportId}" data-status="REJECT">거절</button>
                        </td>
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

    const totalPages = pageData.totalPages;
    const currentPage = pageData.number;

    if (totalPages <= 1) return;

    // 이전 버튼
    if (currentPage > 0) {
        const prev = $(`
            <li class="page-item">
                <a class="page-link" href="#" data-page="${currentPage - 1}"><i class="bi bi-chevron-left"></i></a>
            </li>
        `);
        prev.on("click", function (e) {
            e.preventDefault();
            loadReports(currentPage - 1);
        });
        pagination.append(prev);
    }

    // 페이지 숫자 (최대 5개만 표시)
    const maxVisible = 5;
    const startPage = Math.max(0, currentPage - Math.floor(maxVisible / 2));
    const endPage = Math.min(totalPages, startPage + maxVisible);

    for (let i = startPage; i < endPage; i++) {
        const active = i === currentPage ? "active" : "";
        const li = $(`
            <li class="page-item ${active}">
                <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
            </li>
        `);
        li.on("click", function (e) {
            e.preventDefault();
            loadReports(i);
        });
        pagination.append(li);
    }

    // 다음 버튼
    if (currentPage < totalPages - 1) {
        const next = $(`
            <li class="page-item">
                <a class="page-link" href="#" data-page="${currentPage + 1}"><i class="bi bi-chevron-right"></i></a>
            </li>
        `);
        next.on("click", function (e) {
            e.preventDefault();
            loadReports(currentPage + 1);
        });
        pagination.append(next);
    }
}

$(document).on("click", ".update-status", function () {
    const reportId = $(this).data("id");
    const newStatus = $(this).data("status");

    if (!confirm(`이 신고를 ${newStatus === "COMPLETED" ? "승인" : "거절"} 처리하시겠습니까?`)) return;
    const url = newStatus === "COMPLETED"
        ? `/admin/report/approve/${reportId}`
        : `/admin/report/reject/${reportId}`;

    $.ajax({
        url: url,
        type: "POST",
        success: function (res) {
            showCommonModal("처리 완료", res); // 서버 메시지 그대로 표시
            loadReports(); // 리스트 갱신
        },
        error: function (err) {
            console.error(err);
            showCommonModal("서버 오류", "상태 변경 중 문제가 발생했습니다.");
        }
    });
});
$(document).ready(function () {
    loadReports();
});
