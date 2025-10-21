// $(document).ajaxSend(function(e, xhr, options) {
//     const token = $("meta[name='_csrf']").attr("content");
//     const header = $("meta[name='_csrf_header']").attr("content");
//     if (token && header) xhr.setRequestHeader(header, token);
// });
document.addEventListener("DOMContentLoaded", () =>{
    let selectedCategoryId = null;
    let currentPage = 0;
    let pageSize = 10;

    //날짜 포맷 함수
    function formatDate(dateString) {
        if (!dateString) return "";
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }

    // 테이블 갱신 함수
    function renderTable(data, page=0) {
        const tbody = $("table tbody");
        tbody.empty();

        if (!data || data.length === 0) {
            tbody.append(`<tr><td colspan="8" class="text-center">검색 결과가 없습니다.</td></tr>`);
            return;
        }

        data.forEach((lec, i) => {
            const price = lec.price === 0 ? "무료" : lec.price;
            const row = `
                <tr class="text-center">
                    <td>${i + 1 + page * pageSize}</td>
                    <td>${lec.title}</td>
                    <td>${lec.tutorName}</td>
                    <td>${formatDate(lec.reservationStart)} ~ ${formatDate(lec.reservationEnd)}</td>
                    <td>${formatDate(lec.lectureStart)} ~ ${formatDate(lec.lectureEnd)}</td>
                    <td>${lec.reservationCount}/${lec.totalCount}</td>
                    <td>${price}</td>
                    <td>
                        <a href="/admin/lecture/edit?lectureId=${lec.lectureId}" class="btn btn-sm btn-primary">수정</a>
                        <button class="btn btn-sm btn-danger" onclick="deleteLecture(${lec.lectureId})">삭제</button>
                    </td>
                </tr>
            `;
            tbody.append(row);
        });
    }
    //페이징 렌더링 함수
    function renderPagination(totalPages, currentPageIndex) {
        const pagination = $("#pagination");
        pagination.empty();

        if(totalPages <= 1) return;

        for (let i = 0; i < totalPages; i++) {
            const active = (i === currentPageIndex) ? "active" : "";
            const pageItem = `
            <li class="page-item ${active}">
                <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
            </li>`;
            pagination.append(pageItem);
        }
        //중복 방지 후 이벤트 등록
        $(".page-link").off("click").on("click", function (e) {
            e.preventDefault();
            const page = parseInt($(this).data("page"));
            searchLectures(page);
        });
    }

    // AJAX 검색 함수
    window.searchLectures = function (page=0) {
        currentPage = page;

        const valOrNull = (v) => (v && v.trim() !== "" ? v : null);
        const params = {
            categoryId: selectedCategoryId,
            keyword: valOrNull($("#keyword").val()),
            priceType: valOrNull($("#priceSelect").val()),
            status: valOrNull($("#statusSelect").val()),
            lectureStartDate: valOrNull($("#lectureStart").val()),
            lectureEndDate: valOrNull($("#lectureEnd").val()),
            reservationStartDate: valOrNull($("#reservationStart").val()),
            reservationEndDate: valOrNull($("#reservationEnd").val()),
            page: page,
            size: pageSize
        };

        $.ajax({
            url: "/admin/lecture/search",
            type: "GET",
            data: params,
            success: function (data) {
                console.log("검색 결과:", data);
                renderTable(data.content, page);
                renderPagination(data.totalPages, data.number);
            },
            error: function (err) {
                console.error("검색 실패:", err);
            }
        });
    }

    // 카테고리 버튼 클릭 이벤트
    $("#categoryGroup").on("click", ".btn", function () {
        $("#categoryGroup .btn").removeClass("active");
        $(this).addClass("active");
        selectedCategoryId = $(this).data("category-id") || null;
        searchLectures(0);
    });

    // 검색 버튼 클릭 이벤트
    $("#searchBtn").on("click", function () {
        searchLectures(0);
    });






});