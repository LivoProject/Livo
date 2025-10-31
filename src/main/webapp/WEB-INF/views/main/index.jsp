<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/main.css">
<script src="/js/main.js"></script>

<!-- 컨텐츠 -->

<div id="main">
    <!-- 메인 슬라이드-->
    <section id="hero-tagline" class="text-center">
        <div class="container">
            <h2 class="title">
                새로운 배움, 지금 시작하세요! <br/>
                많은 학습자들이 함께 성장하고 있어요.
            </h2>
            <a href="#" class="btn-point">지금 시작하기</a>
        </div>
    </section>

    <!-- 공지사항 롤링 -->
    <section id="noticeRolling">
        <div>
            <div class="notice-container swiper mySwiper">
                <div class="notice-icon">
                    <i class="bi bi-megaphone-fill"></i>
                    <span class="notice-title">공지사항</span>
                </div>

                <div class="swiper-wrapper">
                    <c:forEach var="notice" items="${notices}">
                        <div class="swiper-slide">
                            <a href="/notice/content?id=${notice.id}">
                                <span class="notice-text">${notice.title}</span>
                                <small class="notice-date">${notice.createdAt}</small>
                            </a>
                        </div>
                    </c:forEach>
                </div>

                <div class="custom_btn_wrap">
                    <button id="swiperPrev" class="btn-swiper"><i class="bi bi-chevron-up"></i></button>
                    <button id="swiperNext" class="btn-swiper"><i class="bi bi-chevron-down"></i></button>
                    <button id="swiperToggle" class="btn-swiper"><i class="bi bi-pause-fill"></i></button>
                </div>
            </div>
        </div>
    </section>

    <!-- 추천 강좌 -->
    <section id="recommend" class="recommend">
        <div class="container">
            <h3>추천 강좌</h3>
            <div class="recommend-grid">

                <c:forEach var="lecture" items="${recommendedLectures}">
                    <a href="/lecture/content/${lecture.lectureId}" class="card popular-card">
                        <div>
                            <img src="${lecture.thumbnailUrl}">
                        </div>
                        <button class="play-btn">
                            <i class="bi bi-play-fill"></i>
                        </button>
                        <div class="card-body justify-content-between" style="gap:0;">
                            <h6 class="fw-bold text-ellipsis-2 mb-2 lh-base">${lecture.title}</h6>
                            <p class="text-muted mb-2">${lecture.tutorName}</p>
                            <span class="mb-2"><fmt:formatNumber value="${lecture.price}" type="number"/>원</span>
                            <div class="card-review">
                                <div>
                                    <span>⭐4.8</span>
                                    <span>(22)</span>
                                </div>
                                <div>
                                    <i class="bi bi-person-fill"></i>
                                    <span>${lecture.reservationCount}</span>
                                </div>
                            </div>
                        </div>
                    </a>
                </c:forEach>

            </div>
        </div>
    </section>


    <!-- 카테고리 -->
    <section id="categories">
        <div class="container text-center">
            <h3>카테고리</h3>
            <div class="category-grid">

                <c:forEach var="category" items="${categories}" varStatus="status">
                    <a href="/lecture/search?mainCategory=${category.categoryId}" class="category-box">


                        <i class="
               <c:choose>
                  <c:when test='${status.index == 0}'>bi bi-laptop</c:when>
                  <c:when test='${status.index == 1}'>bi bi-book</c:when>
                  <c:when test='${status.index == 2}'>bi bi-film</c:when>
                  <c:when test='${status.index == 3}'>bi bi-person-arms-up</c:when>
                  <c:when test='${status.index == 4}'>bi bi-translate</c:when>
                  <c:when test='${status.index == 5}'>bi bi-people</c:when>
                  <c:when test='${status.index == 6}'>bi bi-postcard</c:when>
                  <c:when test='${status.index == 7}'>bi bi-cash-coin</c:when>
                  <c:otherwise>bi bi-star</c:otherwise>
               </c:choose>
            ">
                        </i>
                        <p>${category.categoryName}</p>
                    </a>
                </c:forEach>

            </div>
        </div>
    </section>


    <!-- 인기 강좌 -->
    <section id="popular">
        <div class="container">
            <div class="swiper mySwiper">
                <h3>인기 강좌</h3>
                <div class="swiper-wrapper">
                    <c:forEach var="lectureP" items="${popularLectures}">
                        <!-- -->
                        <div class="swiper-slide">
                            <a href="/lecture/content/${lectureP.lectureId}" class="card popular-card">
                                <div>
                                    <img src="${lectureP.thumbnailUrl}">
                                </div>
                                <button class="play-btn">
                                    <i class="bi bi-play-fill"></i>
                                </button>
                                <div class="card-body justify-content-between" style="gap:0;">
                                    <span class="badge bg-danger">HOT</span>
                                    <h6 class="fw-bold text-ellipsis-2 mb-2 lh-base">${lectureP.title}</h6>
                                    <p class="text-muted mb-2">${lectureP.tutorName}</p>
                                    <span class="mb-2"><fmt:formatNumber value="${lectureP.price}" type="number"/>원</span>
                                    <div class="card-review">
                                        <div>
                                            <span>⭐4.8</span>
                                            <span>(22)</span>
                                        </div>
                                        <div>
                                            <i class="bi bi-person-fill"></i>
                                            <span>${lectureP.reservationCount}</span>
                                        </div>
                                    </div>
                                </div>
                            </a>
                        </div>
                        <!-- -->
                    </c:forEach>
                </div>
                <!-- 커스텀 버튼 -->
                <div class="custom_btn_wrap fade-in-up">
                    <button id="popularPrev" class="btn-swiper">
                        <i class="bi bi-chevron-left"></i>
                    </button>
                    <button id="popularNext" class="btn-swiper">
                        <i class="bi bi-chevron-right"></i>
                    </button>
                </div>
            </div>
        </div>
    </section>


    <!-- 중간 배너 -->
    <section id="midBanner">
        <div class="banner-content">
            <h2 class="fw-bold mb-3">무엇을 배워볼까 고민 중이라면? </h2>
            <p class="mb-3">다양한 인기 강좌를 지금 만나보세요.</p>
            <a href="/lecture/list" class="btn-outline-main">강좌 보러가기</a>
        </div>
    </section>


    <!-- 공지사항 -->
    <section id="info">
        <div class="container">
            <div class="board notice-board">
                <div class="card-header">
                    <h3>공지사항</h3>
                    <a href=/notice/content"><i class="bi bi-plus-lg"></i></a>
                </div>
                <c:forEach var="notice" items="${notices}">
                    <div class="notice-list">
                        <a href="/notice/content?id=${notice.id}">
                            <span>${notice.title}</span>
                            <small>${notice.createdAt}</small>
                        </a>
                    </div>
                </c:forEach>
            </div>
        </div>
    </section>


<%@ include file="/WEB-INF/views/common/footer.jsp" %>