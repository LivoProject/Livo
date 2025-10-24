<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 강의 -->
    <main class="main-content">
        <h3>즐겨찾는 강의</h3>
        <div class="lecture-grid large">
            <!-- 카드 1 -->
            <c:if test="${not empty likedLectures}">
                <c:forEach var="lecture" items="${likedLectures}">
                    <div class="card">
                        <a href="/lecture/content/${lecture.lectureId}">
                            <img src="${lecture.thumbnailUrl}" class="card-img-top" alt="${lecture.title}">

                            <div class="card-body">
                                <h5 class="card-title">${lecture.title}</h5>
                                <p>${lecture.tutorName}∣<fmt:formatNumber value="${lecture.price}" type="number"/> 원</p>
                                <div class="progress" style="height: 8px;">
                                    <div class="progress-bar bg-success"
                                         style="width: ${lecture.progressPercent}%;"></div>
                                </div>
                            </div>
                        </a>
                        <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                            <div>
                                <c:choose>
                                    <c:when test="${lecture.reserved}">
                                        <a href="/lecture/view/${lecture.lectureId}"
                                           class="btn btn-outline-success btn-sm">
                                            ▶ 이어보기
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn-unlike btn-main btn-sm"
                                                data-lecture-id="${lecture.lectureId}"
                                                data-bs-toggle="modal"
                                                data-bs-target="#likeModal">해제
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                                <button class="btn-unlike btn-main" data-lecture-id="${lecture.lectureId}"
                                        data-bs-toggle="modal" data-bs-target="#likeModal">해제
                                </button>
                            </div>
                            <small class="text-muted">9 mins</small>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
            <c:if test="${empty likedLectures}">
                <p class="text-muted">좋아요 강의가 없습니다.</p>
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
        <div class="modal fade" id="likeModal" tabindex="-1" aria-labelledby="likeModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header"><h5 class="modal-title" id="likeModalLabel">공통 모달 제목</h5>
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