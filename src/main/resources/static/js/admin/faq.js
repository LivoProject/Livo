$(document).ajaxSend(function (e, xhr, options) {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    if (token && header) xhr.setRequestHeader(header, token);
});
$(document).ready(function () {
    loadFaq(0);
});

function loadFaq(page) {
    $.ajax({
        url: "/admin/faq/list",
        type: "GET",
        data: { page: page, size: 9 },
        success: function(res) {
            const tbody = $("#faqTableBody");
            tbody.empty();

            res.content.forEach((faq, i) => {
                const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                tbody.append(`
                <tr class="text-center">
                    <td>${res.pageable.pageNumber * 9 + i + 1}</td>
                    <td class="text-start">${faq.question}</td>
                    <td class="text-start">${faq.answer.length > 70 ? faq.answer.substring(0, 70) + "..." : faq.answer}</td>
                    <td class="text-center">
                        <div class="d-flex justify-content-center gap-2">
                            <a href="/admin/faq/edit?id=${faq.id}" class="btn btn-sm btn-primary">수정</a> 
                            <button type="submit" class="btn btn-sm btn-danger delete-faq-btn" data-id="${faq.id}">삭제</button>
                        </div>
                    </td>
                </tr>
                `);
            });

            renderPagination(res);
        }
    });
}
$(document).on("click", ".delete-faq-btn", function () {
    const faqId = $(this).data("id");

    if (!confirm("정말 삭제하시겠습니까?")) return;

    $.ajax({
        url: `/admin/faq/delete/${faqId}`,
        type: "POST",
        success: function () {
            alert("삭제 되었습니다.");
            loadFaq(0); //
        }
    });
});
function renderPagination(res) {
    const pagination = $("#pagination");
    pagination.empty();

    const totalPages = res.totalPages;
    const currentPage = res.number;

    if (totalPages <= 1) return;

    // 이전 버튼
    if (currentPage > 0) {
        const prev = $(`
            <li class="page-item">
                <a class="page-link" href="#" data-page="${currentPage - 1}">
                    <i class="bi bi-chevron-left"></i>
                </a>
            </li>
        `);
        prev.on("click", function (e) {
            e.preventDefault();
            loadFaq(currentPage - 1);
        });
        pagination.append(prev);
    }

    // 페이지 숫자 (최대 5개 표시)
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
            loadFaq(i);
        });
        pagination.append(li);
    }

    // 다음 버튼
    if (currentPage < totalPages - 1) {
        const next = $(`
            <li class="page-item">
                <a class="page-link" href="#" data-page="${currentPage + 1}">
                    <i class="bi bi-chevron-right"></i>
                </a>
            </li>
        `);
        next.on("click", function (e) {
            e.preventDefault();
            loadFaq(currentPage + 1);
        });
        pagination.append(next);
    }
}
