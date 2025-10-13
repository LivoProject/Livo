<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>PROJECT</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" />
    <style>
        body {
            background-color: #f8f9fa;
        }
        .faq-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .faq-container h2 {
            text-align: center;
            margin-bottom: 30px;
        }
        .btn-group {
            text-align: center;
            margin-top: 20px;
        }
        .message {
            text-align: center;
            margin-top: 15px;
        }
    </style>
    <!-- Swiper -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
    <!-- Custom CSS -->
    <link rel="stylesheet" href="css/reset.css" />
    <link rel="stylesheet" href="css/common.css" />
    <link rel="stylesheet" href="css/main.css" />
    <link rel="stylesheet" href="css/sub.css" />
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Swiper JS -->
    <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
    <!-- Custom js -->
    <script src="js/common.js"></script>
    <script src="js/main.js"></script>
    <script src="js/sub.js"></script>
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
                        <a class="nav-link" href="#">홈</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">강좌</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">공지사항</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">마이페이지</a>
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

<!-- 컨텐츠는 여기서 작성하세요 -->
<section id="sub" class="container">
    <div class="faq-container">
        <h2>FAQ 등록</h2>

        <form action="/faq/add" method="post">
            <div class="mb-3">
                <label for="question" class="form-label">질문</label>
                <textarea class="form-control" id="question" name="question" rows="3" required></textarea>
            </div>
            <div class="mb-3">
                <label for="answer" class="form-label">답변</label>
                <textarea class="form-control" id="answer" name="answer" rows="5" required></textarea>
            </div>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">등록</button>
                <button type="reset" class="btn btn-secondary">초기화</button>
            </div>
        </form>

        <!-- <c:if test="${not empty message}">
            <p class="message text-success">${message}</p>
        </c:if> -->

        <div class="text-center mt-3">
            <a href="ask" class="text-decoration-none">&larr; 메인으로</a>
        </div>
    </div>
</section>
<!-- 컨텐츠 끝 -->

<!-- Footer -->
<footer>
    <div>
        <p>NONAME ⓒ 2025 All Rights Reserved.</p>
    </div>
</footer>
</body>
</html>