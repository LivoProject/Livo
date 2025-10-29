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
            const isFree = lec.isFree === true || lec.isFree === 1 || lec.price === 0;
            const price = isFree ? "무료" : lec.price;
            const reservationPeriod = isFree ? "무제한" : `${formatDate(lec.reservationStart)} ~ ${formatDate(lec.reservationEnd)}`;
            const lecturePeriod = isFree ? "무제한" : `${formatDate(lec.lectureStart)} ~ ${formatDate(lec.lectureEnd)}`;
            const totalCount = isFree ? "무제한" : `${lec.reservationCount}/${lec.totalCount}`;
            const row = `
                <tr class="text-center">
                    <td>${i + 1 + page * pageSize}</td>
                    <td>${lec.title}</td>
                    <td>${lec.tutorName}</td>
                    <td>${reservationPeriod}</td>
                    <td>${lecturePeriod}</td>
                    <td>${totalCount}</td>
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

        if (totalPages <= 1) return;

        // 이전 버튼
        if (currentPageIndex > 0) {
            pagination.append(`
            <li class="page-item">
                <a class="page-link" href="#" data-page="${currentPageIndex - 1}"><i class="bi bi-chevron-left"></i></a>
            </li>
        `);
        }

        // 페이지 숫자 (최대 5개만 보이게)
        const maxVisible = 5;
        const startPage = Math.max(0, currentPageIndex - Math.floor(maxVisible / 2));
        const endPage = Math.min(totalPages, startPage + maxVisible);

        for (let i = startPage; i < endPage; i++) {
            const active = (i === currentPageIndex) ? "active" : "";
            pagination.append(`
            <li class="page-item ${active}">
                <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
            </li>
        `);
        }

        // 다음 버튼
        if (currentPageIndex < totalPages - 1) {
            pagination.append(`
            <li class="page-item">
                <a class="page-link" href="#" data-page="${currentPageIndex + 1}"><i class="bi bi-chevron-right"></i></a>
            </li>
        `);
        }

        // 이벤트 등록 (중복 방지)
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

    window.deleteLecture = function (lectureId) {
        if (!confirm("정말로 이 강의를 삭제하시겠습니까?")) return;

        $.ajax({
            url: `/admin/lecture/delete/${lectureId}`,
            type: "POST",
            beforeSend: function(xhr) {
                // CSRF 토큰 설정 (Spring Security 적용 시)
                const token = $("meta[name='_csrf']").attr("content");
                const header = $("meta[name='_csrf_header']").attr("content");
                if (token && header) xhr.setRequestHeader(header, token);
            },
            success: function (res) {
                if (res.success) {
                    alert("강의가 삭제되었습니다.");
                    if ($("table tbody tr").length === 1 && currentPage > 0) {
                        searchLectures(currentPage - 1);
                    } else {
                        searchLectures(currentPage);
                    }
                } else {
                    alert("삭제 실패: " + (res.message || "알 수 없는 오류"));
                }
            },
            error: function (xhr) {
                console.error("삭제 오류:", xhr);
                alert("삭제 중 오류가 발생했습니다.");
            }
        });
    };
    searchLectures(0);
});
