$(document).ajaxSend(function (e, xhr, options) {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    if (token && header) xhr.setRequestHeader(header, token);
});
$(document).ready(function () {
    loadNotices(0);

    // 검색
    $("form[action*='/admin/notice/list']").on("submit", function (e) {
        e.preventDefault();
        loadNotices(0);
    });
});

function loadNotices(page = 0) {
    const q = $("input[name='q']").val() || "";

    $.ajax({
        url: "/admin/notice/list/data",
        type: "GET",
        data: {page: page, size: 10, q: q},
        success: function (pageData) {
            const tbody = $("#noticeBody");
            tbody.empty();

            if (pageData.content.length === 0) {
                tbody.append(`<tr><td colspan="4" class="text-center py-4">등록된 공지사항이 없습니다.</td></tr>`);
                $("#pagination").empty();
                return;
            }

            pageData.content.forEach((n, idx) => {
                const num = page * pageData.size + idx + 1;
                const date = n.createdAt ? n.createdAt.substring(0, 16).replace('T', ' ') : '-';
                const pinnedBadge = n.pinned ? `<span class="badge bg-success me-1">고정</span>` : '';
                const visibleText = n.visible ? '' : ' · <span class="text-danger">비노출</span>';
                const nickName = n.nickname ? n.nickname : '알 수 없음';
                const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                tbody.append(`

                    <tr>
                      <td class="text-center">${num}</td>
                       <td class="text-center">
                        ${pinnedBadge}
                        </td>
                      <td class="text-start">
                        ${n.title}
                        </td>
                        <td class="text-start">
                            <div class="text-truncate">
                                ${n.content}
                            </div>
                        </td>
                        <td class="text-center">
                             ${date}
                        </td>  
                        <td class="text-center">
                            ${n.viewCount}
                        </td>
                      <td>
                        <a href="/admin/notice/${n.id}/edit" class="btn btn-sm btn-primary me-1">수정</a>
                        <form action="/admin/notice/${n.id}" method="post" style="display:inline-block;"
                              onsubmit="return confirm('정말 삭제하시겠습니까?');">
                          <input type="hidden" name="_method" value="DELETE">
                          <input type="hidden" name="_csrf" value="${csrfToken}">
                          <button type="submit" class="btn btn-sm btn-danger">삭제</button>
                        </form>
                      </td>
                    </tr>
                    
                 
                `);
            });

            renderPagination(pageData);
        },
        error: function (err) {
            console.error("공지 불러오기 실패:", err);
        }
    });
}

function renderPagination(pageData) {
    const pagination = $("#pagination");
    pagination.empty();

    const totalPages = pageData.totalPages;
    const currentPage = pageData.number;

    if (totalPages <= 1) return;

    if (!pageData.first) {
        pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${currentPage - 1}"><i class="bi bi-chevron-left"></i></a></li>`);
    }

    for (let i = 0; i < totalPages; i++) {
        const active = i === currentPage ? "active" : "";
        pagination.append(`<li class="page-item ${active}"><a class="page-link" href="#" data-page="${i}">${i + 1}</a></li>`);
    }

    if (!pageData.last) {
        pagination.append(`<li class="page-item"><a class="page-link" href="#" data-page="${currentPage + 1}"><i class="bi bi-chevron-right"></i></a></li>`);
    }

    $(".page-link").off("click").on("click", function (e) {
        e.preventDefault();
        const page = parseInt($(this).data("page"));
        loadNotices(page);
    });
}
