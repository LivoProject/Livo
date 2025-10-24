<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>


<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">
    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 메인 컨텐츠 -->
    <main class="main-content">
        <h3>수강평 관리</h3>


        <div class="review-grid">
            <c:if test="${not empty reviews}">
                <c:forEach var="review" items="${reviews}">
                    <div class="card review-card" data-review-id="${review.reviewUId}"
                         data-lecture-id="${review.reservation.lecture.lectureId}"
                         data-review-content="${review.reviewContent}"
                         data-review-star="${review.reviewStar}">
                        <div class="card-body">
                            <h6 class="fw-bold">${review.reservation.lecture.title}</h6>
                            <p class="text-muted small">별점: ${review.reviewStar} ★</p>
                            <p class="text-truncate">${review.reviewContent}</p>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
            <c:if test="${empty reviews}">
                <p class="text-muted">작성한 리뷰가 없습니다.</p>
            </c:if>
        </div>

        <nav class="pagination justify-content-center mt-4">
            <c:if test="${page != null and not page.first}">
                <a class="page-link" href="?page=${page.number - 1}">이전</a>
            </c:if>

            <c:if test="${page != null and page.totalPages > 0}">
                <c:forEach var="i" begin="0" end="${page.totalPages - 1}">
                    <a class="page-link ${page.number == i ? 'active' : ''}" href="?page=${i}">${i + 1}</a>
                </c:forEach>
            </c:if>

            <c:if test="${page != null and not page.last}">
                <a class="page-link" href="?page=${page.number + 1}">다음</a>
            </c:if>
        </nav>

    </main>
</section>

<!-- 리뷰 보기 모달 -->
<div class="modal fade" id="reviewModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">리뷰 내용</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p id="modalReviewContent"></p>
                <p class="text-muted" id="modalReviewStar"></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancel" data-bs-dismiss="modal">닫기</button>
                <button type="button" class="btn-main" id="reviewMoreBtn">더보기</button>
            </div>
        </div>
    </div>
</div>
<!-- 컨텐츠 끝 -->

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
