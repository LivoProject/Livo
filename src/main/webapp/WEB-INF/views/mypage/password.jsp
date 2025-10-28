<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="/css/mypage.css">

<html>
<head>
    <title>비밀번호 변경</title>

    <!-- CSRF -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <!-- CSS -->
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/reset.css">
    <link rel="stylesheet" href="/css/form.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Bootstrap Icons CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        .valid {
            color: #198754;
            font-size: 0.9rem;
        }

        .error {
            color: #dc3545;
            font-size: 0.9rem;
        }

        .pw-card {
            background: #fff;
            border-radius: 16px;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
            max-width: 420px;
            margin: 80px auto;
            padding: 40px 36px;
        }

        h3 {

            font-size: 2rem;
            font-weight: 700;
            margin: 5rem 0 2rem;
            position: relative;
        }

        label {
            font-size: 0.9rem;
            font-weight: 500;
            color: #333;
        }

        .form-control {
            border-radius: 8px;
            height: 44px;
            font-size: 0.95rem;
        }

        .btn-submit {
            background-color: var(--color-main, #17b4c7);
            border: none;
            border-radius: 8px;
            padding: 10px;
            color: white;
            font-weight: 600;
            font-size: 1rem;
            transition: background 0.2s ease;
        }

        .btn-submit:disabled {
            background-color: #b0dfe6;
            cursor: not-allowed;
        }

        .btn-submit:hover:not(:disabled) {
            background-color: #14a3b4;
        }

        .text-center a {
            color: var(--color-main, #17b4c7);
            text-decoration: none;
            font-size: 0.9rem;
        }

        .valid {
            color: #198754;
            font-size: 0.85rem;
        }

        .error {
            color: #dc3545;
            font-size: 0.85rem;
        }

        .alert {
            border-radius: 8px;
            padding: 10px;
            font-size: 0.9rem;
        }
        .input-wrap {
            position: relative;
        }
        .input-wrap button {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
        }
        #mypagePw {
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        #mypagePw #pwForm {
            width: 450px;
        }
    </style>
</head>

<body>
<div class="container" id="mypagePw">
    <h3 class="mb-4 text-center">비밀번호 변경</h3>

    <form id="pwForm" action="/mypage/password" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <!-- 에러 / 성공 메시지 -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <!-- 현재 비밀번호 -->
        <div class="mb-3">
            <label class="form-label">현재 비밀번호</label>
            <div class="input-wrap">
                <input type="password" name="currentPassword" id="currentPassword" class="form-control pw-input" required>
                <button class="password-toggle" type="button">
                    <i class="bi bi-eye"></i>
                </button>
            </div>
        </div>

        <!-- 새 비밀번호 -->
        <div class="mb-3">
            <label class="form-label">새 비밀번호</label>
            <div class="input-wrap">
                <input type="password" name="newPassword" id="newPassword" class="form-control pw-input" required>
                <button class="password-toggle" type="button">
                    <i class="bi bi-eye"></i>
                </button>
            </div>
            <small id="pwMsg" class="form-text"></small>
        </div>

        <!-- 새 비밀번호 확인 -->
        <div class="mb-3">
            <label class="form-label">새 비밀번호 확인</label>
            <div class="input-wrap">
                <input type="password" name="confirmPassword" id="confirmPassword" class="form-control pw-input" required>
                <button class="password-toggle" type="button">
                    <i class="bi bi-eye"></i>
                </button>
            </div>
            <small id="confirmMsg" class="form-text"></small>
        </div>

        <!-- 버튼 -->
        <div class="d-grid">
            <button type="submit" id="submitBtn" class="btn-main" disabled>변경</button>
        </div>

        <!-- 돌아가기 -->
        <div class="text-center mt-3">
            <a href="/mypage/info" class="text-secondary">← 내 정보로 돌아가기</a>
        </div>
    </form>
</div>

<!-- JS -->
<script src="/js/mypage-password.js"></script>
</body>
</html>
