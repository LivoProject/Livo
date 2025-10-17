<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>비밀번호 변경</title>

    <!-- CSRF -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/reset.css">
    <link rel="stylesheet" href="/css/form.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>

<body>
<div class="container mt-5" style="max-width: 400px;">
    <h3 class="mb-4 text-center">비밀번호 변경</h3>

    <form action="/mypage/password" method="post">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

        <!-- 에러/성공 메시지 -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>

        <div class="mb-3">
            <label class="form-label">현재 비밀번호</label>
            <input type="password" name="currentPassword" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">새 비밀번호</label>
            <input type="password" name="newPassword" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">새 비밀번호 확인</label>
            <input type="password" name="confirmPassword" class="form-control" required>
        </div>

        <div class="d-grid">
            <button type="submit" class="btn btn-primary">변경</button>
        </div>

        <div class="text-center mt-3">
            <a href="/mypage/info" class="text-secondary">← 내정보로 돌아가기</a>
        </div>
    </form>
</div>
</body>
</html>
