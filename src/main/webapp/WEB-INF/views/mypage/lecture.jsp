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
                        <a href="/lecture/view/${reservations.lectureId}">
                            <img
                                    src="${reservations.thumbnailUrl}"
                                    class="card-img-top"
                                    alt="강의 썸네일"
                            />
                            <div class="card-body">
                                <h6 class="fw-bold mb-1">${reservations.title}</h6>
                                <p class="text-muted small mb-2">${reservations.tutorName}</p>
                                <div class="progress" style="height: 8px;">
                                    <div class="progress-bar bg-success"
                                         style="width: ${reservations.progressPercent}%;"></div>
                                </div>
                            </div>
                        </a>
                        <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                            <div>
                                <a href="/lecture/view/${reservations.lectureId}" class="btn-main">이어보기</a>
                                <button class="btn-main"
                                        data-bs-toggle="modal"
                                        data-bs-target="#exampleModal">
                                    수강평 작성
                                </button>
                                <button class="btn-unreserve btn-main"
                                        data-lecture-id="${reservations.lectureId}"
                                        data-bs-toggle="modal"
                                        data-bs-target="#reserveModal">
                                    예약 취소
                                </button>
                            </div>
                            <small class="text-muted">9 mins</small>
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

        <!-- 모달 -->
        <div class="modal fade" id="reserveModal" tabindex="-1" aria-labelledby="reserveModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header"><h5 class="modal-title" id="reserveModalLabel">공통 모달 제목</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
                    </div>
                    <div class="modal-body"> 이곳은 모달 내용입니다.<br> 설명이나 폼, 알림 메시지 등을 넣을 수 있습니다.</div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" data-bs-dismiss="modal">취소</button>
                        <button type="button" class="btn-main">확인</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- // 모달 -->

    </main>


</section>
<!-- 컨텐츠 끝 -->

<%@ include file="/WEB-INF/views/common/footer.jsp" %>