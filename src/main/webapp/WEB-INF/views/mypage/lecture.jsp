<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>


<!-- 컨텐츠-->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 강의 -->
    <main class="main-content">
        <h3>내 강의실</h3>
        <div class="lecture-grid large">
            <c:if test="${not empty reservations}">
                <c:forEach var="reservations" items="${reservations}">

                    <div class="card">
                        <div class="card-img-wrap">
                            <a href="/lecture/view/${reservations.lectureId}">
                                <img src="${reservations.thumbnailUrl}" class="card-img-top" alt="강의 썸네일"/>
                                <button class="play-btn">
                                    <i class="bi bi-play-fill"></i>
                                </button>
                            </a>
                        </div>
                        <div class="card-body">

                            <a href="/lecture/content/${reservations.lectureId}">
                                <h6 class="fw-bold mb-1">${reservations.title}</h6>
                                <p class="mb-2">${reservations.tutorName}</p>
                                <div class="progress" style="height: 8px;">
                                    <div class="progress-bar bg-success"
                                         style="width: ${reservations.progressPercent}%;"></div>
                                </div>
                                <small class="text-muted">${reservations.progressPercent}%</small>
                            </a>
                        </div>

                        <div class="card-footer">

                            <div class="button-wrap">
                                <a href="/lecture/view/${reservations.lectureId}" class="btn-main">이어보기</a>
                                <button class="btn-unreserve btn-cancel"
                                        data-lecture-id="${reservations.lectureId}"
                                        data-bs-toggle="modal"
                                        data-bs-target="#reserveModal">
                                    예약 취소
                                </button>
                                <a href="/lecture/content/${reservations.lectureId}#review" class="btn-cancel">
                                    수강평 작성
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>

            <c:if test="${empty reservations}">
                <p class="text-muted">예약한 강의가 없습니다.</p>
            </c:if>

        </div>

        <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center mt-4">
                <c:if test="${!page.first}">
                    <li class="page-item">
                        <a class="page-link" href="?page=${page.number - 1}">이전</a>
                    </li>
                </c:if>

                <%-- totalPages가 0일 때는 forEach 실행 안 함 --%>
                <c:if test="${page.totalPages > 0}">
                    <c:forEach var="i" begin="0" end="${page.totalPages - 1}">
                        <li class="page-item ${page.number == i ? 'active' : ''}">
                            <a class="page-link" href="?page=${i}">${i + 1}</a>
                        </li>
                    </c:forEach>
                </c:if>

                <c:if test="${!page.last}">
                    <li class="page-item">
                        <a class="page-link" href="?page=${page.number + 1}">다음</a>
                    </li>
                </c:if>
            </ul>
        </nav>

    </main>

</section>
<!-- 컨텐츠 끝 -->


<!-- 모달 -->
<%@ include file="/WEB-INF/views/common/modal.jsp" %>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>