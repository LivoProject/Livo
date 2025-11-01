<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>비밀번호 변경 | LiVO</title>

    <!-- CSRF -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <!-- CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet"/>
    <link rel="stylesheet" href="<c:url value='/css/reset.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/common.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/auth/login.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/form.css'/>">
</head>

<body id="loginAndReg">
<div class="pw-container">

    <form id="pwForm"  class="register-container" action="/mypage/password" method="post">

        <h2>비밀번호 변경</h2>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

        <!-- 알림 -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <!-- 현재 비밀번호 -->
        <div class="field">
            <label>현재 비밀번호</label>
            <div class="input-wrap">
                <input type="password" class="pw-input" name="currentPassword" id="currentPassword" required placeholder="현재 비밀번호 입력">
                <button class="password-toggle" type="button"><i class="bi bi-eye"></i></button>
            </div>
        </div>

        <!-- 새 비밀번호 -->
        <div class="field">
            <label>새 비밀번호</label>
            <div class="input-wrap">
                <input type="password" class="pw-input" name="newPassword" id="newPassword" required placeholder="새 비밀번호 (8~20자)">
                <button class="password-toggle" type="button"><i class="bi bi-eye"></i></button>
            </div>
            <small id="pwMsg" class="form-text"></small>
        </div>

        <!-- 비밀번호 확인 -->
        <div class="field">
            <label>새 비밀번호 확인</label>
            <div class="input-wrap">
                <input type="password" class="pw-input" name="confirmPassword" id="confirmPassword" required placeholder="비밀번호 확인">
                <button class="password-toggle" type="button"><i class="bi bi-eye"></i></button>
            </div>
            <small id="confirmMsg" class="form-text"></small>
        </div>

        <button type="submit" class="btn-submit mt-3">변경</button>

        <p><a href="/mypage/info">← 내 정보로 돌아가기</a></p>
    </form>
</div>

<script src="/js/mypage-password.js"></script>
</body>
</html>
