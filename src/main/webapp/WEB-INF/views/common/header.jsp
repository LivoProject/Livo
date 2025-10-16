<%@ page contentType="text/html;charset=UTF-8" language="java" %> />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <title>LiVO</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <!-- 파비콘 (브라우저 탭 아이콘) -->
    <link
      rel="shortcut icon"
      href="/img/common/favicon.ico"
      type="image/x-icon"
    />

    <!-- 오픈그래프 (SNS 공유 시 표시되는 미리보기 정보)
    <meta name="robots" content="noindex" />
    <meta property="og:title" content="LiVO" />
    <meta property="og:description" content="온라인 강의" />
    <meta property="og:image" content=" livo-maria.cfm6wkquapqe.ap-northeast-2.rds.amazonaws.com/img/common/og-img.png" />
    <meta property="og:url" content=" livo-maria.cfm6wkquapqe.ap-northeast-2.rds.amazonaws.com" />
    <meta property="og:type" content="website" /> -->

    <!-- Bootstrap -->
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"
    />
    <!-- Swiper -->
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css"
    />
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/reset.css" />
    <link rel="stylesheet" href="/css/common.css" />
    <link rel="stylesheet" href="/css/main.css" />
    <link rel="stylesheet" href="/css/mypage.css" />
    <link rel="stylesheet" href="/css/lecture.css" />
    <link rel="stylesheet" href="/css/modal.css" />
    <link rel="stylesheet" href="/css/form.css" />
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Swiper JS -->
    <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
    <!-- Custom js -->
    <script src="/js/common.js"></script>
    <script src="/js/main.js"></script>
  </head>
  <body>
    <!-- 헤더 -->
    <header>
      <div class="container">
        <nav class="navbar navbar-expand-lg">
          <a class="logo navbar-brand logo" href="/">
            <img src="/img/common/logo.svg" alt="로고" />
          </a>
          <!-- 모바일 토글 버튼 -->
          <button
            class="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#mainNavbar"
          >
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
                <a class="nav-link" href="${pageContext.request.contextPath}/mypage">마이페이지</a>
              </li>
              <li>
                <button id="searchToggle" class="nav-link">
                  <i class="bi bi-search"></i>
                </button>
              </li>
            </ul>

            <!-- 버튼 영역 -->
              <div class="header-actions">
                  <!-- 로그인 버튼 -->
                  <a class="btn btn-link" href="<c:url value='/auth/login'/>">
                      <i class="bi bi-box-arrow-in-right"></i> 로그인
                  </a>
                  <!-- 회원가입 버튼 -->
                  <a class="btn btn-link" href="<c:url value='/auth/register'/>">
                      <i class="bi bi-person-plus"></i> 회원가입
                  </a>
              </div>
          </div>
        </nav>
      </div>
    </header>

    <!-- 헤더 검색창 -->
    <div id="headerSearch">
      <h4>배우고 싶은 강좌를 찾아보세요.</h4>
      <div class="input-group">
        <input
          type="text"
          class="form-control"
          placeholder="강좌명, 대학명, 키워드 입력"
        />
        <button>
          <i class="bi bi-search"></i>
        </button>
      </div>
    </div>