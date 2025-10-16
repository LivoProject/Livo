<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8" />
    <title>PROJECT</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" />
    <!-- Swiper -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
    <!-- Custom CSS -->
    <link rel="stylesheet" href="css/reset.css" />
    <link rel="stylesheet" href="css/common.css" />
    <link rel="stylesheet" href="css/main.css" />
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Swiper JS -->
    <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
    <!-- Custom js -->
    <script src="js/common.js"></script>
    <script src="js/main.js"></script>
</head>

<body>
<!-- 헤더 -->
<!-- 헤더 -->
<header>
    <div class="container">
        <nav class="navbar navbar-expand-lg">
            <a class="logo navbar-brand" href="#">LOGO</a>

            <!-- 모바일 토글 버튼 -->
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar">
                <span class="navbar-toggler-icon"></span>
            </button>

            <!-- 메뉴 + 버튼 -->
            <div class="collapse navbar-collapse" id="mainNavbar">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="index.html">홈</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="sub.html">강좌</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="sub.html">공지사항</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="sub.html">마이페이지</a>
                    </li>
                    <li>
                        <button id="searchToggle" class="nav-link">
                            <i class="bi bi-search"></i>
                        </button>
                    </li>
                </ul>

                <!-- 버튼 영역 (collapse 안으로 이동) -->
                <div class="header-actions">
                    <button><i class="bi bi-box-arrow-in-right"></i>로그인</button>
                    <button><i class="bi bi-person-plus"></i>회원가입</button>
                </div>
            </div>
        </nav>
    </div>
</header>

<div id="main">

         <c:forEach var="notice" items="${notices}">

            <div class="swiper-slide">
              <span>${notice.title}</span>
              <small><fmt:formatDate value="${notice.createdAt}" pattern="yyyy-MM-dd"/></small>

    <!-- 헤더 검색창 -->
    <div id="headerSearch">
        <h4>배우고 싶은 강좌를 찾아보세요.</h4>
        <div class="input-group">
            <input type="text" class="form-control" placeholder="강좌명, 대학명, 키워드 입력" />
            <button>
                <i class="bi bi-search"></i>
            </button>
        </div>
    </div>

    <!-- 메인 슬라이드-->
    <section id="mainSlide" class="carousel slide" data-bs-ride="carousel">
        <div class="carousel-inner carousel-fade">
            <div class="carousel-item active">
                <div class="carousel-img">
                    <p>슬라이드1</p>
                </div>
            </div>
            <div class="carousel-item">
                <div class="carousel-img">
                    <p>슬라이드2</p>
                </div>
            </div>
            <div class="carousel-item">
                <div class="carousel-img">
                    <p>슬라이드3</p>
                </div>
            </div>
            <!-- 커스텀 인디케이터 -->
            <div class="custom-indicator">
                <!-- 숫자 인디케이터 -->
                <div class="number-indicators">
                    <button data-bs-target="#mainSlide" data-bs-slide-to="0" class="active">
                        01
                    </button>
                    <button data-bs-target="#mainSlide" data-bs-slide-to="1">
                        02
                    </button>
                    <button data-bs-target="#mainSlide" data-bs-slide-to="2">
                        03
                    </button>
                </div>
                <!-- 프로그래스바 -->
                <div class="progress-bar">
                    <div class="progress"></div>
                </div>
                <button id="togglePlay" class="play-btn">
                    <i class="bi bi-pause"></i>
                </button>
            </div>
        </div>

    </section>

    <!-- 공지사항 롤링 -->
    <section id="noticeRolling">
        <div class="swiper mySwiper container">
            <i class="bi bi-megaphone-fill"></i>
            <div class="swiper-wrapper">
                <div class="swiper-slide">
                    <span>2025년 산업맞춤 단기직무능력인증과정 선정 결과</span>
                    <small>2025.05.13</small>
                </div>
                <div class="swiper-slide">
                    <span>2025년 산업맞춤 단기직무능력인증과정 선정 결과</span>
                    <small>2025.05.13</small>
                </div>

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

    <!-- 메인 검색 -->
    <section id="MainSearch">
        <div class="container fade-in-up">
            <div class="input-group">
                <input type="text" class="form-control" placeholder="강좌명, 대학명, 키워드 입력" />
                <button>
                    <i class="bi bi-search"></i>
                </button>
                <div class="tag-wrap">
                    <span>#해시태그</span>
                    <span>#해시태그</span>
                </div>
            </div>
        </div>
    </section>

    <!-- 추천 강좌 -->
    <section id="recommend">
        <div class="swiper mySwiper container">
            <h3>추천 강좌</h3>
            <div class="swiper-wrapper">
                <!--  -->
                <div class="swiper-slide">
                    <div class="swiper-slide">
                        <a href="#" class="card popular-card">
                            <img src="img/common/no-image.png" class="no-image" />
                            <div class="card-body">
                                <h6>AI 딥러닝 기초</h6>
                                <p>POSTECH | 장하준 교수</p>
                                <p>⭐ 4.8 (1,200명 수강)</p>
                            </div>
                        </a>
                    </div>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <div class="swiper-slide">
                        <a href="#" class="card popular-card">
                            <img src="img/common/no-image.png" class="no-image" />
                            <div class="card-body">
                                <h6>AI 딥러닝 기초</h6>
                                <p>POSTECH | 장하준 교수</p>
                                <p>⭐ 4.8 (1,200명 수강)</p>
                            </div>
                        </a>
                    </div>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <div class="swiper-slide">
                        <a href="#" class="card popular-card">
                            <img src="img/common/no-image.png" class="no-image" />
                            <div class="card-body">
                                <h6>AI 딥러닝 기초</h6>
                                <p>POSTECH | 장하준 교수</p>
                                <p>⭐ 4.8 (1,200명 수강)</p>
                            </div>
                        </a>
                    </div>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <div class="swiper-slide">
                        <a href="#" class="card popular-card">
                            <img src="img/common/no-image.png" class="no-image" />
                            <div class="card-body">
                                <h6>AI 딥러닝 기초</h6>
                                <p>POSTECH | 장하준 교수</p>
                                <p>⭐ 4.8 (1,200명 수강)</p>
                            </div>
                        </a>
                    </div>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <div class="swiper-slide">
                        <a href="#" class="card popular-card">
                            <img src="img/common/no-image.png" class="no-image" />
                            <div class="card-body">
                                <h6>AI 딥러닝 기초</h6>
                                <p>POSTECH | 장하준 교수</p>
                                <p>⭐ 4.8 (1,200명 수강)</p>
                            </div>
                        </a>
                    </div>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <div class="swiper-slide">
                        <a href="#" class="card popular-card">
                            <img src="img/common/no-image.png" class="no-image" />
                            <div class="card-body">
                                <h6>AI 딥러닝 기초</h6>
                                <p>POSTECH | 장하준 교수</p>
                                <p>⭐ 4.8 (1,200명 수강)</p>
                            </div>
                        </a>
                    </div>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <div class="swiper-slide">
                        <a href="#" class="card popular-card">
                            <img src="img/common/no-image.png" class="no-image" />
                            <div class="card-body">
                                <h6>AI 딥러닝 기초</h6>
                                <p>POSTECH | 장하준 교수</p>
                                <p>⭐ 4.8 (1,200명 수강)</p>
                            </div>
                        </a>
                    </div>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <div class="swiper-slide">
                        <a href="#" class="card popular-card">
                            <img src="img/common/no-image.png" class="no-image" />
                            <div class="card-body">
                                <h6>AI 딥러닝 기초</h6>
                                <p>POSTECH | 장하준 교수</p>
                                <p>⭐ 4.8 (1,200명 수강)</p>
                            </div>
                        </a>
                    </div>
                </div>
                <!--  -->
            </div>
            <!-- 커스텀 버튼 -->
            <button id="recommendPrev" class="btn-swiper">
                <i class="bi bi-chevron-left"></i>
            </button>
            <button id="recommendNext" class="btn-swiper">
                <i class="bi bi-chevron-right"></i>
            </button>
            <div class="swiper-pagination"></div>
        </div>
    </section>

    <!-- 아이콘 카테고리 -->
    <section id="categories">
        <div class="container">
            <h3 class="fade-in-up">카테고리</h3>
            <div class="row fade-in-up">
                <!--  -->
                <div class="col-4 col-md-2">
                    <a href="# " class="category-box">
                        <p>인문</p>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="col-4 col-md-2">
                    <a href="# " class="category-box">
                        <p>인문</p>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="col-4 col-md-2">
                    <a href="# " class="category-box">
                        <p>인문</p>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="col-4 col-md-2">
                    <a href="# " class="category-box">
                        <p>인문</p>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="col-4 col-md-2">
                    <a href="# " class="category-box">
                        <p>인문</p>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="col-4 col-md-2">
                    <a href="# " class="category-box">
                        <p>인문</p>
                    </a>
                </div>
                <!--  -->
            </div>
        </div>
    </section>

    <!-- 인기 강좌 -->
    <section id="popular">
        <div class="swiper mySwiper container" style="overflow: visible">
            <h3>인기 강좌</h3>
            <div class="swiper-wrapper">
                <!--  -->
                <div class="swiper-slide">
                    <a href="#" class="card popular-card">
                        <img src="img/common/no-image.png" class="no-image" />
                        <div class="card-body">
                            <span class="badge bg-danger">HOT</span>
                            <h6>AI 딥러닝 기초</h6>
                            <p>POSTECH | 장하준 교수</p>
                            <p>⭐ 4.8 (1,200명 수강)</p>
                        </div>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <a href="#" class="card popular-card">
                        <img src="img/common/no-image.png" class="no-image" />
                        <div class="card-body">
                            <span class="badge bg-danger">HOT</span>
                            <h6>AI 딥러닝 기초</h6>
                            <p>POSTECH | 장하준 교수</p>
                            <p>⭐ 4.8 (1,200명 수강)</p>
                        </div>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <a href="#" class="card popular-card">
                        <img src="img/common/no-image.png" class="no-image" />
                        <div class="card-body">
                            <span class="badge bg-danger">HOT</span>
                            <h6>AI 딥러닝 기초</h6>
                            <p>POSTECH | 장하준 교수</p>
                            <p>⭐ 4.8 (1,200명 수강)</p>
                        </div>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <a href="#" class="card popular-card">
                        <img src="img/common/no-image.png" class="no-image" />
                        <div class="card-body">
                            <span class="badge bg-danger">HOT</span>
                            <h6>AI 딥러닝 기초</h6>
                            <p>POSTECH | 장하준 교수</p>
                            <p>⭐ 4.8 (1,200명 수강)</p>
                        </div>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <a href="#" class="card popular-card">
                        <img src="img/common/no-image.png" class="no-image" />
                        <div class="card-body">
                            <span class="badge bg-danger">HOT</span>
                            <h6>AI 딥러닝 기초</h6>
                            <p>POSTECH | 장하준 교수</p>
                            <p>⭐ 4.8 (1,200명 수강)</p>
                        </div>
                    </a>
                </div>
                <!--  -->
                <!--  -->
                <div class="swiper-slide">
                    <a href="#" class="card popular-card">
                        <img src="img/common/no-image.png" class="no-image" />
                        <div class="card-body">
                            <span class="badge bg-danger">HOT</span>
                            <h6>AI 딥러닝 기초</h6>
                            <p>POSTECH | 장하준 교수</p>
                            <p>⭐ 4.8 (1,200명 수강)</p>
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
        <ul class="list-unstyled">
            <c:forEach var="notice" items="${notices}">
              <li>
                <a href="#">
                  <span>${notice.title}</span><small><fmt:formatDate value="${notice.createdAt}" pattern="yyyy-MM-dd"/></small>
                </a>
              </li>
          </c:forEach>
        </ul>
      </div>
    </div>
  </section>

    </section>

    <!-- 배너 -->
    <section id="midBanner"></section>

    <!-- 게시판 + 정보 -->
    <section id="info">
        <div class="container fade-in-up">
            <div class="board">
                <div class="title-wrap">
                    <h3>공지사항</h3>
                    <a href="#"><i class="bi bi-plus-lg"></i></a>
                </div>
                <ul>
                    <li>
            <span>2025년 2학기 수강신청 안내2025년 2학기 수강신청 안내2025년
              2학기 수강신청 안내2025년 2학기 수강신청 안내2025년 2학기
              수강신청 안내2025년 2학기 수강신청 안내2025년 2학기 수강신청
              안내2025년 2학기 수강신청 안내</span>
                        <small>2020.02.02</small>
                    </li>
                    <li>
                        <span>서버 점검 공지</span>
                        <small>2020.02.02</small>
                    </li>
                    <li>
                        <span>신규 강좌 오픈 예정</span>
                        <small>2020.02.02</small>
                    </li>
                    <li>
                        <span>서버 점검 공지</span>
                        <small>2020.02.02</small>
                    </li>
                </ul>
            </div>
            <div class="etc">
                <p>자주묻는질문</p>
                <p>강좌문의</p>
                <p>자료실</p>
                <p>이용가이드</p>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer>
        <div>
            <p>NONAME ⓒ 2025 All Rights Reserved.</p>
        </div>
    </footer>

</div>
</body>

</html>