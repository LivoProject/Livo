<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>회원가입</title>

    <!-- CSRF -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <!-- 서버 세션에 저장된 인증된 이메일 (없으면 빈 값) -->
    <meta name="verifiedEmail" content="${verifiedEmail != null ? verifiedEmail : ''}"/>

    <c:url var="urlRegister"   value="/auth/register"/>
    <c:url var="urlValEmail"   value="/auth/validate/email"/>
    <c:url var="urlValNick"    value="/auth/validate/nickname"/>
    <c:url var="urlValPw"      value="/auth/validate/password"/>
    <c:url var="urlSendCode"   value="/auth/send-code"/>
    <c:url var="urlVerifyCode" value="/auth/verify-code"/>
    <c:url var="urlLogin"      value="/auth/login"/>

    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/reset.css">
    <link rel="stylesheet" href="/css/form.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
</head>

<body id="register">
<div class="register-container">
    <h2>회원가입</h2>

    <form:form id="signupForm" method="post" modelAttribute="signUpRequest" action="${urlRegister}">

    <!-- 이메일 -->
        <div class="field">
            <label class="form-label">이메일</label>
            <form:input path="email" placeholder="name@example.com" cssClass="form-control" id="email"/>
            <small id="verifyInfo" class="text-success"></small>

            <div class="mt-2 d-flex align-items-center gap-2">
                <button type="button" id="btnSendCode" class="btn btn-outline-primary btn-sm">인증코드 전송</button>
                <span id="sendCountdown" class="text-muted" style="display:none;"></span>
            </div>

            <span data-msg="email"></span>
        </div>

        <!-- 인증 코드 -->
        <div id="codeBlock" class="field" style="display:none;">
            <label class="form-label">인증 코드</label>
            <div class="d-flex align-items-center gap-2">
                <input type="text" id="code" class="form-control" placeholder="6자리" maxlength="6" inputmode="numeric"/>
                <button type="button" id="btnVerifyCode" class="btn btn-primary btn-sm">코드 확인</button>
            </div>
            <small id="codeMsg" class="form-text"></small>
        </div>

        <!-- 비밀번호 -->
        <div class="field">
            <label class="form-label">비밀번호</label><br/>
            <div class="position-relative">
                <form:password path="password" cssClass="form-control" placeholder="영문·숫자·특수문자 8~20자" id="password"/>
                <span data-msg="password"></span>
                <button class="password-toggle" type="button" aria-label="비밀번호 표시 토글">보기</button>
            </div>
        </div>

        <!-- 닉네임 -->
        <div class="field">
            <label class="form-label">닉네임</label><br/>
            <form:input path="nickname" cssClass="form-control" placeholder="홍이" id="nickname"/>
            <span data-msg="nickname"></span>
        </div>

        <!-- 이름 -->
        <div class="field">
            <label class="form-label">이름</label><br/>
            <form:input path="name" cssClass="form-control" placeholder="홍길동" id="name"/>
        </div>

        <!-- 전화 -->
        <div class="field">
            <label class="form-label">전화</label><br/>
            <form:input path="phone" cssClass="form-control" placeholder="010-1234-5678" id="phone"/>
        </div>

        <!-- 생년월일 -->
        <div class="field">
            <label class="form-label">생년월일</label><br/>
            <form:input path="birth" placeholder="yyyy-mm-dd" cssClass="form-control" id="birth"/>
        </div>

        <!-- 성별 -->
        <div class="field">
            <label class="form-label">성별</label><br/>
            <div class="gender-group">
                <label class="gender-radio"><input type="radio" name="gender" value="M"/> <span>남성</span></label>
                <label class="gender-radio"><input type="radio" name="gender" value="F"/> <span>여성</span></label>
            </div>
        </div>

        <div class="field">
            <!-- <button type="submit" class="btn-submit">가입하기</button> -->
            <button type="submit" id="btnSubmit" class="btn-submit">가입하기</button>
        </div>
    </form:form>

    <p>이미 회원이신가요? <a href="<c:url value='/auth/login'/>">로그인</a></p>
</div>

<script>
    window.__REG_CFG__ = {
        urlRegister  : "${urlRegister}",
        urlValEmail  : "${urlValEmail}",
        urlValNick   : "${urlValNick}",
        urlValPw     : "${urlValPw}",
        urlSendCode  : "${urlSendCode}",
        urlVerifyCode: "${urlVerifyCode}",
        urlLogin     : "${urlLogin}"

    };
</script>
<script src="/js/register.js?v=5"></script>
</body>
</html>
