<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">
    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 메인 컨텐츠 -->
    <main class="main-content">
        <!-- 프로필 / 환영 -->
        <div class="welcome-box mb-4">
            <div>
                <h3>${mypage.username} 님, 반가워요!</h3>
                <p>🌱 <span>${mypage.joinDays}</span>일 째 성장 중</p>
            </div>
            <p>
                진행 중인 강의 <span>${inProgressLectureCount}</span>개 · 이번 주 학습시간 <span>${weeklyStudyHours}</span>시간
            </p>
        </div>

        <!-- 학습 현황 -->
        <div class="card-box-wrap mb-4">
            <div class="card-box recent-card">
                <div class="card-header">
                    <h6>최근 학습 강의</h6>
                    <a href="/mypage/lecture" class="more-link">
                        학습 목록 <i class="bi bi-chevron-right"></i>
                    </a>
                </div>
                <div class="recent-lecture">
                    <div class="play-icon">
                        <i class="bi bi-play-fill"></i>
                    </div>

                    <c:if test="${recentLecture != null}">
                        <div class="text-wrap">
                            <h5>${recentLecture.lecture.title}</h5>
                            <p>진도율: ${recentLecture.progressPercent}%</p>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- 학습 통계 -->
            <div class="card-box summary-card">
                <h6 class="">학습 통계</h6>
                <div class="row">
                    <div class="col">
                        <div class="stat-item">
                            <div class="fs-4 fw-bold text-primary">
                                ${totalStudyHours}시간
                            </div>
                            <small class="text-muted">누적 학습시간</small>
                        </div>
                    </div>
                    <div class="col">
                        <div class="stat-item">
                            <div class="fs-4 fw-bold text-success">
                                ${completedLectures}개
                            </div>
                            <small class="text-muted">완강 강좌</small>
                        </div>
                    </div>
                    <div class="col">
                        <div class="stat-item">
                            <div class="fs-4 fw-bold text-warning">
                                ${studyDays}일
                            </div>
                            <small class="text-muted">이번 달 학습일수</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 알림 / 공지 -->
        <div class="card-box mb-4">
            <div class="card-header">
                <h6>📢 공지사항</h6>
                <a href="notice/list" class="more-link"
                >더보기 <i class="bi bi-chevron-right"></i
                ></a>
            </div>
            <ul>
                <c:if test="${not empty notices}">
                    <c:forEach var="notice" items="${notices}">
                        <li>
                            <a href="/notice/content?id=${notice.id}">
                                <span>${notice.title}</span>
                                <small>${notice.createdAt}</small>
                            </a>
                        </li>
                    </c:forEach>
                </c:if>
                <c:if test="${empty notices}">
                    <p class="text-muted">공지사항이 없습니다.</p>
                </c:if>
            </ul>
        </div>

        <!-- 즐겨찾기 -->
        <div class="card-box mb-4">
            <div class="card-header">
                <h6>⭐ 즐겨찾는 강의</h6>
                <a href="mypage/like" class="more-link">더보기 <i class="bi bi-chevron-right"></i></a>
            </div>
            <div class="lecture-grid">
                <c:if test="${not empty top2LikedLectures}">
                    <c:forEach var="lecture" items="${top2LikedLectures}">
                        <div class="card">

                            <div class="card-img-wrap">
                                <a href="/lecture/view/${lecture.lectureId}">
                                    <img src="${lecture.thumbnailUrl}" class="card-img-top" alt="${lecture.title}">
                                    <button class="play-btn">
                                        <i class="bi bi-play-fill"></i>
                                    </button>

                                </a>

                            </div>
                            <div class="card-body">
                                <a href="/lecture/content/${lecture.lectureId}">
                                    <h6 class="card-title">${lecture.title}</h6>
                                    <p>${lecture.tutorName}∣<fmt:formatNumber value="${lecture.price}"
                                                                              type="number"/>
                                        원</p>
                                    <div class="progress" style="height: 8px;">
                                        <div class="progress-bar bg-success"
                                             style="width: ${lecture.progressPercent}%;"></div>
                                    </div>

                                    <small class="text-muted">${lecture.progressPercent}%</small>
                                </a>
                            </div>

                            <div class="card-footer">
                                <div>
                                    <button class="btn-unlike btn-main" data-lecture-id="${lecture.lectureId}"
                                            data-bs-toggle="modal" data-bs-target="#likeModal">해제
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${empty top2LikedLectures}">
                    <p class="text-muted">좋아요 강의가 없습니다.</p>
                </c:if>


            </div>
        </div>

        <!-- 추천 -->
        <div class="card-box mb-4">
            <div class="card-header">
                <h6>⭐ 추천 (여긴 나중에 결제내역으로 바꾸는게 날듯 )</h6>
                <%--                <a href="/myp" class="more-link"--%>
                <%--                >더보기 <i class="bi bi-chevron-right"></i--%>
                <%--                ></a>--%>
            </div>
            <div class="row">

                <c:forEach var="lecture" items="${recommendedLectures}">
                    <div class="col-md-4">
                        <div class="lecture-card">${lecture.title}</div>
                    </div>
                </c:forEach>

            </div>
        </div>


    </main>

</section>
<!-- 컨텐츠 끝 -->


<%@ include file="/WEB-INF/views/common/modal.jsp" %>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
