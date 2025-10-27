<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>로그인</title>
    <!-- 파비콘 (브라우저 탭 아이콘) -->
    <link rel="shortcut icon" href="/img/common/favicon.ico" type="image/x-icon"/>

    <!-- 스타일 -->
    <link rel="stylesheet" href="/css/auth/reset.css"/>
    <link rel="stylesheet" href="/css/auth/login.css"/>
    <!-- Bootstrap Icons CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <script src="/js/common.js"></script>

    <!-- (AJAX 사용 시) CSRF 메타 -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
</head>
<body id="loginAndReg">
<!-- 상단 메시지 -->
<c:if test="${param.error != null}">
    <div style="color:red">이메일 또는 비밀번호가 올바르지 않습니다.</div>
</c:if>
<c:if test="${param.logout != null}">
    <div style="color:green">정상적으로 로그아웃되었습니다.</div>
</c:if>
<c:if test="${param.registered != null}">
    <div style="color:blue">회원가입이 완료되었습니다. 로그인 해주세요.</div>
</c:if>
<c:if test="${not empty msg}">
    <div style="color:blue">${msg}</div>
</c:if>

<!-- 로그인 카드 -->
<form method="post" action="<c:url value='/auth/login'/>" accept-charset="UTF-8" autocomplete="on">

    <h2>
        <img src="/img/common/logo.svg" alt="LiVO">
    </h2>

    <div>
        <label for="email">Email</label>
        <div class="input-wrap">
            <i class="bi bi-person"></i>
            <input id="email" type="email" name="email" placeholder="example@example.com" required />
        </div>
    </div>

    <div>
        <label for="password">Password</label>
        <div class="input-wrap">
            <i class="bi bi-lock"></i>

            <input id="password" class="pw-input" type="password" name="password" placeholder="비밀번호 입력" required/>
            <button class="toggle-password" type="button">
                <i class="bi bi-eye"></i>
            </button>
        </div>
    </div>

    <!-- CSRF 토큰 -->
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div style="margin-top:10px">
        <button type="submit" id="loginBtn">로그인</button>
    </div>

    <!-- 구분선 -->
    <div class="or-sep" aria-hidden="true"><span>또는</span></div>

    <!-- 소셜 로그인 (폼 내부) -->
    <div class="social-grid">
        <!-- Google -->
        <a class="btn-social google" href="<c:url value='/oauth2/authorization/google'/>" aria-label="구글로 로그인">
            <svg class="ico" viewBox="0 0 48 48" aria-hidden="true">
                <path fill="#FFC107"
                      d="M44.5 20H24v8.5h11.8C34.8 33.7 30.1 37 24 37c-7.2 0-13-5.8-13-13s5.8-13 13-13c3.1 0 6 1.1 8.2 3l6-6C34.4 4.3 29.5 2.5 24 2.5 12 2.5 2.5 12 2.5 24S12 45.5 24 45.5c11.5 0 21-8.3 21-21 0-1.6-.2-3.1-.5-4.5z"/>
                <path fill="#FF3D00"
                      d="M6.3 14.7l6.9 5c1.9-4 6.1-6.8 10.8-6.8 3.1 0 6 1.1 8.2 3l6-6C34.4 4.3 29.5 2.5 24 2.5 16 2.5 9.1 7 6.3 14.7z"/>
                <path fill="#4CAF50"
                      d="M24 45.5c6.1 0 11.4-2.2 15.1-5.9l-6.9-5.6c-2 1.4-4.5 2.2-7.2 2.2-6.1 0-11.3-4.1-13-9.7l-7 5.4C7.8 39.7 15.3 45.5 24 45.5z"/>
                <path fill="#1976D2"
                      d="M45 24c0-1.6-.2-3.1-.5-4.5H24V28h11.8c-.9 2.7-2.8 5-5.3 6.5l6.9 5.6C41.9 36.4 45 30.7 45 24z"/>
            </svg>
            <span>Google로 계속하기</span>
        </a>

        <!-- Kakao
        <a class="btn-social kakao" href="<c:url value='/oauth2/authorization/kakao'/>" aria-label="카카오로 로그인">
            <svg class="ico" viewBox="0 0 24 24" aria-hidden="true">
                <path fill="#3A1D1D"
                      d="M12 3.5c-4.97 0-9 3.06-9 6.84 0 2.35 1.64 4.4 4.08 5.6L6 18.9c-.06.26.21.47.45.35l3.25-1.7c.41.05.84.07 1.3.07 4.97 0 9-3.06 9-6.84S16.97 3.5 12 3.5z"/>
            </svg>
            <span>Kakao로 계속하기</span>
        </a> -->

    </div>

    <div class="login-footer">
        <p>계정이 없으신가요? <a href="/auth/register">회원가입</a></p>
        <a href="<c:url value='/'/>">메인으로</a>
    </div>
    </div>
</form>

<!-- (선택) 계정 연결 결과 배너 -->
<c:if test="${param.linked ne null}">
    <div class="alert success">계정 연결이 완료되었습니다. 소셜 계정으로 로그인할 수 있어요.</div>
</c:if>
<c:if test="${param.cancel ne null}">
    <div class="alert info">계정 연결을 취소했어요.</div>
</c:if>

<!--  브금 재생용 로그인 클릭 트리거 -->
<script>
    document.querySelectorAll('#loginBtn, .oauth-btn, a[href^="/oauth2/"], a[href^="/login/oauth2/"]')
        .forEach(el => el.addEventListener('click', () => {
            try {
                sessionStorage.setItem('ALLOW_BGM', '1');
            } catch (e) {
            }
        }));
</script>
</body>
</html>
