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
    <sc class="main-content">
        <!-- 프로필 / 환영 -->
        <div class="welcome-box mb-4">
            <div>
                <h3>${mypage.username} 님, 반가워요!</h3>
                <p>🌱 <span>${mypage.joinDays}</span>일 째 성장 중</p>
            </div>
            <p>
                진행 중인 강의 <span>3</span>개 · 이번 주 학습시간 <span>4</span>시간
            </p>
        </div>

        <!-- 학습 현황 -->
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="card-box recent-card">
                    <div class="card-header">
                        <h6>최근 학습 강의</h6>
                        <a href="#" class="more-link">
                            학습 목록 <i class="bi bi-chevron-right"></i>
                        </a>
                    </div>
                    <div class="recent-lecture">
                        <div class="play-icon">
                            <i class="bi bi-play-fill"></i>
                        </div>
                        <div class="lecture-info">
                            <h6 class="lecture-title">Svelte.js 입문 가이드</h6>
                            <p><strong>9강</strong> / 11강 (81.82%)</p>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="card-box">
                    <h6>학습 진행률</h6>
                    <div class="progress">
                        <div class="progress-bar" style="width: 70%"></div>
                    </div>
                    <p>이번 주 학습 <span>2</span>시간</p>
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
                <c:forEach var="notice" items="${notices}">
                    <li>
                        <a href="#">
                            <span>${notice.title}</span>
                            <small>${notice.createdAt}</small>
                        </a>
                    </li>
                </c:forEach>
            </ul>
        </div>

        <!-- 즐겨찾기 -->
        <div class="card-box mb-4">
            <div class="card-header">
                <h6>⭐ 즐겨찾는 강의</h6>
                <a href="mypage/like" class="more-link">더보기 <i class="bi bi-chevron-right"></i></a>
            </div>
            <div class="lecture-grid">
                <c:forEach var="lecture" items="${top2LikedLectures}">
                    <div class="card">
                        <img src="${lecture.thumbnailUrl}" class="card-img-top" alt="${lecture.title}">

                        <div class="card-body">
                            <h5 class="card-title">${lecture.title}</h5>
                            <p>${lecture.tutorName}∣<fmt:formatNumber value="${lecture.price}" type="number"/> 원</p>
                            <div class="progress mt-2">
                                <div class="progress-bar" style="width: 60%"></div>
                            </div>
                        </div>
                        <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                            <div>
                                <button class="btn-unlike btn-main" data-lecture-id="${lecture.lectureId}">해제
                                </button>
                            </div>
                            <small class="text-muted">9 mins</small>
                        </div>
                    </div>
                </c:forEach>


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
    </sc>

</section>
<!-- 컨텐츠 끝 -->

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
