<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8" />
    <title>dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" />

    <!-- Swiper -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
    <!-- Custom CSS -->
    <link rel="stylesheet" href="css/reset.css" />
    <link rel="stylesheet" href="css/common.css" />
    <link rel="stylesheet" href="css/main.css" />
    <link rel="stylesheet" href="css/sub.css" />
    <link rel="stylesheet" href="css/dashboard.css" />
    <!-- Swiper JS -->
    <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.3.2/dist/chart.umd.js"></script>
    <!-- Custom js -->
    <!-- <script src="js/common.js"></script>
    <script src="js/main.js"></script>
    <script src="js/sub.js"></script> -->
</head>
<body>
<div class="g-sidenav-show bg-gray-100">
    <aside class="sidenav navbar navbar-vertical navbar-expand-xs border-0 border-radius-xl my-3 fixed-start ms-3 ps ps--active-y bg-white d-none d-xl-block" id="sidenav-main">
        <div class="sidenav-header">
            <i class="fas fa-times p-3 cursor-pointer text-secondary opacity-5 position-absolute end-0 top-0 d-xl-none" aria-hidden="true" id="iconSidenav"></i>
            <a class="navbar-brand m-0" href="#" target="_blank">
                <img src="#" alt="logo">
                <span class="ms-1 font-weight-bold">LiVO</span>
            </a>
        </div>
        <hr class="horizontal dark mt-0">
        <div class="navbar-collapse w-auto max-height-vh-100 h-100 ps" id="sidebarMenu">
            <ul class="nav nav-pills flex-column mb-3">
                <li class="nav-item">
                    <a href="dashboard.html" class="nav-link active"><i class="bi bi-house me-2"></i>대시보드</a>
                </li>
                <li><a href="chartPage.html" class="nav-link"><i class="bi bi-bar-chart me-2"></i>통계</a></li>
                <li><a href="lecturePage.html" class="nav-link"><i class="bi bi-people me-2"></i>강의 관리</a></li>
                <li><a href="reportpage.html" class="nav-link"><i class="bi bi-people me-2"></i>신고 처리</a></li>
                <li><a href="noticepage.html" class="nav-link"><i class="bi bi-gear me-2"></i>공지사항 관리</a></li>
                <li><a href="faqpage.html" class="nav-link"><i class="bi bi-gear me-2"></i>FAQ 관리</a></li>
            </ul>
        </div>
        <div class="ps__rail-y" style="top: 0px; height: 972px; right: 0px;">
            <div class="ps__thumb-y" tabindex="0" style="top: 0px; height: 717px;"></div>
        </div>
    </aside>
    <!-- Mobile Sidebar (Offcanvas) -->
    <div class="offcanvas offcanvas-start bg-white" tabindex="-1" id="sidebarMobile">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title">메뉴</h5>
            <button type="button" class="btn-close text-reset" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div class="offcanvas-body">
            <ul class="nav flex-column">
                <li class="nav-item"><a href="dashboard.html" class="nav-link active"><i class="bi bi-house me-2"></i>대시보드</a></li>
                <li><a href="chartPage.html" class="nav-link"><i class="bi bi-bar-chart me-2"></i>통계</a></li>
                <li><a href="lecturePage.html" class="nav-link"><i class="bi bi-people me-2"></i>강의 관리</a></li>
                <li><a href="reportpage.html" class="nav-link"><i class="bi bi-people me-2"></i>신고 처리</a></li>
                <li><a href="noticepage.html" class="nav-link"><i class="bi bi-gear me-2"></i>공지사항 관리</a></li>
                <li><a href="faqpage.html" class="nav-link"><i class="bi bi-gear me-2"></i>FAQ 관리</a></li>
            </ul>
        </div>
    </div>
