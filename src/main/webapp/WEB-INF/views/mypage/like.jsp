<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage-modal.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 강의 -->
    <main class="main-content">
        <h3>즐겨찾는 강의</h3>

        <!-- 정렬 선택 -->
        <select id="sortSelect" class="form-select w-25 ms-auto mb-3">
            <option value="new" selected>최신순</option>
            <option value="old">오래된순</option>
            <option value="popular">인기순</option>
        </select>
        <div id="likedLectureGrid" class="lecture-grid large">
            <!-- 카드 1 -->
            <c:if test="${not empty likedLectures}">
                <c:forEach var="lecture" items="${likedLectures}">
                    <div class="card">
                        <div class="card-img-wrap">
                            <a href="/lecture/content/${lecture.lectureId}">
                                <img src="${lecture.thumbnailUrl}" class="card-img-top" alt="${lecture.title}"/>
                                <button class="play-btn">
                                    <i class="bi bi-play-fill"></i>
                                </button>
                            </a>
                        </div>

                        <div class="card-body">
                            <a href="/lecture/content/${lecture.lectureId}">
                                <h6 class="fw-bold text-ellipsis-2 lecture-title">${lecture.title}</h6>
                                <p class="text-muted mb-3">${lecture.tutorName}</p>
                                <span><fmt:formatNumber value="${lecture.price}" type="number"/>원</span>
                                <div class="progress" style="height: 8px;">
                                    <div class="progress-bar bg-success"
                                         style="width: ${lecture.progressPercent}%;"></div>
                                </div>
                                <small class="text-muted">${lecture.progressPercent}%</small>
                            </a>
                        </div>

                        <div class="card-footer">
                            <div class="button-wrap">
                                <c:choose>
                                    <c:when test="${lecture.reserved}">
                                        <button class="btn-unlike btn-main"
                                                data-lecture-id="${lecture.lectureId}"
                                                data-bs-toggle="modal"
                                                data-bs-target="#likeModal">
                                            삭제하기
                                        </button>
                                        <a href="/lecture/content/${lecture.lectureId}#review" class="btn-cancel">수강평
                                            작성</a>
                                    </c:when>

                                    <c:otherwise>
                                        <button class="btn-unlike btn-main"
                                                data-lecture-id="${lecture.lectureId}"
                                                data-bs-toggle="modal"
                                                data-bs-target="#likeModal">
                                            삭제하기
                                        </button>
                                    </c:otherwise>
                                </c:choose>

                            </div>
                        </div>
                    </div>

                </c:forEach>
            </c:if>
            <c:if test="${empty likedLectures}">
                <p class="text-muted">좋아요한 강의가 없습니다.</p>
            </c:if>
        </div>

        <%@ include file="/WEB-INF/views/common/pagination.jsp" %>

    </main>

</section>
<!-- 컨텐츠 끝 -->

<script>
    document.addEventListener("DOMContentLoaded", function () {
        const $sort = $("#sortSelect");
        const container = document.getElementById("likedLectureGrid");

        // 🔹 드롭다운 변경 시 호출
        $sort.on("change", function () {
            const sort = $(this).val(); // 'new' | 'old' | 'popular'
            fetchLectures(sort);
        });

        function fetchLectures(sort) {
            const token  = $("meta[name='_csrf']").attr("content");
            const header = $("meta[name='_csrf_header']").attr("content");

            $.ajax({
                url: "/mypage/like/sort",
                type: "POST",
                data: { sort },
                beforeSend: function (xhr) { xhr.setRequestHeader(header, token); },
                success: function (res) {
                    console.log("✅ 정렬 응답:", res);

                    if (!res || res.success === false) {
                        container.innerHTML = "<p class='text-muted'>좋아요한 강의가 없습니다.</p>";
                        return;
                    }

                    const list = res.data || [];
                    if (!list.length) {
                        container.innerHTML = "<p class='text-muted'>좋아요한 강의가 없습니다.</p>";
                        return;
                    }

                    let html = "";
                    list.forEach((r) => {
                        const priceText = (r.price || 0).toLocaleString() + "원";
                        const progressPercent = r.progressPercent ?? 0;

                        // 수강평 작성 버튼 HTML
                        const reviewBtn = r.reserved == 1
                            ? '<a href="/lecture/content/' + r.lectureId + '#review" class="btn-cancel">수강평 작성</a>'
                            : '';

                        html += '<div class="card">' +
                            '<div class="card-img-wrap">' +
                            '<a href="/lecture/content/' + r.lectureId + '">' +
                            '<img src="' + r.thumbnailUrl + '" class="card-img-top" alt="' + r.title + '">' +
                            '<button class="play-btn"><i class="bi bi-play-fill"></i></button>' +
                            '</a>' +
                            '</div>' +
                            '<div class="card-body">' +
                            '<a href="/lecture/content/' + r.lectureId + '">' +
                            '<h6 class="fw-bold text-ellipsis-2 lecture-title">' + r.title + '</h6>' +
                            '<p class="text-muted mb-3">' + r.tutorName + '</p>' +
                            '<span>' + priceText + '</span>' +
                            '<div class="progress" style="height: 8px;">' +
                            '<div class="progress-bar bg-success" style="width: ' + progressPercent + '%;"></div>' +
                            '</div>' +
                            '<small class="text-muted">' + progressPercent + '%</small>' +
                            '</a>' +
                            '</div>' +
                            '<div class="card-footer">' +
                            '<div class="button-wrap">' +
                            '<button class="btn-unlike btn-main" data-lecture-id="' + r.lectureId + '" data-bs-toggle="modal" data-bs-target="#likeModal">삭제하기</button>' +
                            reviewBtn +
                            '</div>' +
                            '</div>' +
                            '</div>';
                    });

                    container.innerHTML = html;

                },
                error: function (xhr) {
                    console.error("❌ 정렬 요청 실패:", xhr);
                    alert("정렬 중 오류가 발생했습니다.");
                },
            });
        }

    });
</script>


<!-- 모달 -->
<%@ include file="/WEB-INF/views/common/modal.jsp" %>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>