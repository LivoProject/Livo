<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- 컨텐츠 -->

<div id="main">
  <!-- 메인 슬라이드-->
  <section id="hero-tagline" class="text-center">
    <div class="container">
      <h2 class="fw-bold mb-2">
        누구나, 언제 어디서나 배울 수 있는 온라인 캠퍼스
      </h2>
      <p class="text-light">
        전국 대학과 전문기관의 강의를 한 곳에서 만나보세요.
      </p>
      <a href="#" class="btn btn-light mt-3 px-4">지금 시작하기</a>
    </div>
  </section>

  <!-- 공지사항 롤링 -->
  <section id="noticeRolling">
    <div class="swiper mySwiper container">
      <i class="bi bi-megaphone-fill"></i>
      <div class="swiper-wrapper">

        <c:forEach var="i" begin="1" end="5">

            <div class="swiper-slide">
              <span>2025년 산업맞춤 단기직무능력인증과정 선정 결과</span>
              <small>2025.05.13</small>
            </div>


    </c:forEach>
  </div>
  <!-- 커스텀 버튼 -->
  <div class="custom_btn_wrap">
    <button id="swiperPrev" class="btn-swiper">
      <i class="bi bi-chevron-up"></i>
    </button>
    <button id="swiperNext" class="btn-swiper">
      <i class="bi bi-chevron-down"></i>
    </button>
    <button id="swiperToggle" class="btn-swiper">
      <i class="bi bi-pause-fill"></i>
    </button>
  </div>
</div>
  </section>

  <!-- 추천 강좌 -->

  <section id="recommend">
    <div class="container">
      <h3>추천 강좌</h3>
      <div class="recommend-grid">

        <c:forEach var="lecture" items="${recommendedLectures}">
        <a href="#" class="card popular-card">
         <div style="width: 100%; background-color: #ddd; height: 200px; border-radius: 12px 12px 0 0;"></div>
          <button class="play-btn">
            <i class="bi bi-play-fill"></i>
          </button>
          <div class="card-body">
            <h6>${lecture.title}</h6>
            <p>${lecture.tutorName}∣<fmt:formatNumber value="${lecture.price}" type="number"/>></p>
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
        <a href="#" class="category-box">
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
          <!--  -->
          <div class="swiper-slide">
            <a href="#" class="card popular-card">
                <div style="width: 100%; background-color: #ddd; height: 200px; border-radius: 12px 12px 0 0;"></div>
              <button class="play-btn">
                <i class="bi bi-play-fill"></i>
              </button>
              <div class="card-body">
                <span class="badge bg-danger">HOT</span>
                <h6>AI 딥러닝 기초</h6>
                <p>POSTECH | 장하준 교수</p>
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
          </div>
          <!--  -->
          <!--  -->
          <div class="swiper-slide">
            <a href="#" class="card popular-card">
                <div style="width: 100%; background-color: #ddd; height: 200px; border-radius: 12px 12px 0 0;"></div>
              <button class="play-btn">
                <i class="bi bi-play-fill"></i>
              </button>
              <div class="card-body">
                <span class="badge bg-danger">HOT</span>
                <h6>AI 딥러닝 기초</h6>
                <p>POSTECH | 장하준 교수</p>
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
          </div>
          <!--  -->
          <!--  -->
          <div class="swiper-slide">
            <a href="#" class="card popular-card">
                <div style="width: 100%; background-color: #ddd; height: 200px; border-radius: 12px 12px 0 0;"></div>
              <button class="play-btn">
                <i class="bi bi-play-fill"></i>
              </button>
              <div class="card-body">
                <span class="badge bg-danger">HOT</span>
                <h6>AI 딥러닝 기초</h6>
                <p>POSTECH | 장하준 교수</p>
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
          </div>
          <!--  -->
          <!--  -->
          <div class="swiper-slide">
            <a href="#" class="card popular-card">
                <div style="width: 100%; background-color: #ddd; height: 200px; border-radius: 12px 12px 0 0;"></div>
              <button class="play-btn">
                <i class="bi bi-play-fill"></i>
              </button>
              <div class="card-body">
                <span class="badge bg-danger">HOT</span>
                <h6>AI 딥러닝 기초</h6>
                <p>POSTECH | 장하준 교수</p>
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
          </div>
          <!--  -->
          <!--  -->
          <div class="swiper-slide">
            <a href="#" class="card popular-card">
                <div style="width: 100%; background-color: #ddd; height: 200px; border-radius: 12px 12px 0 0;"></div>
              <button class="play-btn">
                <i class="bi bi-play-fill"></i>
              </button>
              <div class="card-body">
                <span class="badge bg-danger">HOT</span>
                <h6>AI 딥러닝 기초</h6>
                <p>POSTECH | 장하준 교수</p>
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
          </div>
          <!--  -->
          <!--  -->
          <div class="swiper-slide">
            <a href="#" class="card popular-card">
                <div style="width: 100%; background-color: #ddd; height: 200px; border-radius: 12px 12px 0 0;"></div>
              <button class="play-btn">
                <i class="bi bi-play-fill"></i>
              </button>
              <div class="card-body">
                <span class="badge bg-danger">HOT</span>
                <h6>AI 딥러닝 기초</h6>
                <p>POSTECH | 장하준 교수</p>
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
          </div>
          <!--  -->
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
    <div class="banner-content py-5">
      <h2 class="fw-bold mb-3">지금 인기 강좌 TOP 100 공개!</h2>
      <p>가장 많은 학습자들이 선택한 강의를 만나보세요.</p>
      <a href="#" class="btn btn-outline-light mt-3">강좌 보러가기</a>
    </div>
  </section>


  <!-- 공지사항 -->

  <section id="info">
    <div class="container">
      <div class="board">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <h3>공지사항</h3>
          <a href="#"><i class="bi bi-plus-lg"></i></a>
        </div>
        <ul class="list-unstyled">
          <li>
            <a href="#">
              <span>2025년 2학기 수강신청 안내</span><small>2025.02.02</small>
            </a>
          </li>
          <li>
            <a href="#">
              <span>서버 점검 공지</span><small>2025.02.10</small>
            </a>
          </li>
          <li>
            <a href="#">
              <span>신규 강좌 오픈 예정</span><small>2025.02.15</small>
            </a>
          </li>
          <li>
            <a href="#">
              <span>이용약관 변경 안내</span><small>2025.03.01</small>
            </a>
          </li>
        </ul>
      </div>
    </div>
  </section>


<%@ include file="/WEB-INF/views/common/footer.jsp" %>