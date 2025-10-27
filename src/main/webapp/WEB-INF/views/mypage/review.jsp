<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">
    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 메인 컨텐츠 -->
    <main class="main-content">
        <h3>수강평 관리</h3>

        <div class="review-list">
            <c:if test="${not empty reviews}">
                <c:forEach var="review" items="${reviews}">
                    <div class="review-card"
                         data-review-id="${review.reviewUId}"
                         data-lecture-id="${review.reservation.lecture.lectureId}"
                         data-review-content="${review.reviewContent}"
                         data-review-star="${review.reviewStar}">
                        <!-- 헤더 -->
                        <div class="review-header">
                            <h5>${review.reservation.lecture.title}</h5>
                            <div class="review-stars">
                                <c:forEach var="i" begin="1" end="5">
                                    <i class="bi ${i <= review.reviewStar ? 'bi-star-fill' : 'bi-star'}"></i>
                                </c:forEach>
                            </div>
                        </div>

                        <!-- 본문 -->
                        <div class="review-body">
                            <p>${review.reviewContent}</p>
                        </div>

                        <!-- 푸터 -->
                        <div class="review-footer d-flex justify-content-between align-items-center">
                               <fmt:formatDate value="${review.createdAt}" pattern="yyyy-MM-dd" />
                             <!-- <div class="review-actions">
                                <button class="btn-outline-main btn-sm btn-review-edit" data-review-id="${review.reviewUId}">
                                    수정
                                </button>
                                <button class="btn-cancel btn-sm btn-review-delete" data-review-id="${review.reviewUId}">
                                    삭제
                                </button>
                            </div> -->
                        </div>
                    </div>
                </c:forEach>
            </c:if>

            <c:if test="${empty reviews}">
                <p class="text-muted">작성한 리뷰가 없습니다.</p>
            </c:if>
        </div>

        <%@ include file="/WEB-INF/views/common/pagination.jsp" %>

    </main>
</section>

<!-- 모달 -->
<%@ include file="/WEB-INF/views/common/modal.jsp" %>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
