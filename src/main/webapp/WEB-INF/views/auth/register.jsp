<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <c:url var="urlRegister" value="/auth/register"/>
    <c:url var="urlValEmail" value="/auth/validate/email"/>
    <c:url var="urlValNick" value="/auth/validate/nickname"/>
    <c:url var="urlValPw" value="/auth/validate/password"/>
    <c:url var="urlValPhone" value="/auth/validate/phone"/>
    <c:url var="urlSendCode" value="/auth/send-code"/>
    <c:url var="urlVerifyCode" value="/auth/verify-code"/>
    <c:url var="urlLogin" value="/auth/login"/>


    <link rel="stylesheet" href="<c:url value='/css/reset.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/common.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/form.css'/>">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <!-- Bootstrap Icons CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        [data-msg] {
            display: none;
        }
        [data-msg].error,
        [data-msg].valid {
            display: block;
        }
    </style>
</head>

<body id="loginAndReg">
<div class="register-container">
    <h2>회원가입</h2>

    <form:form id="signupForm" method="post" modelAttribute="signUpRequest" action="${urlRegister}">

        <!-- 이메일 -->
        <%--        <div class="field">--%>
        <%--            <label class="form-label">이메일</label>--%>
        <%--            <form:input path="email" placeholder="name@example.com" cssClass="form-control" id="email"/>--%>
        <%--            <small id="verifyInfo" class="text-success"></small>--%>

        <%--            <div class="mt-2 d-flex align-items-center gap-2">--%>
        <%--                <button type="button" id="btnSendCode" class="btn btn-outline-primary btn-sm">인증코드 전송</button>--%>
        <%--                <span id="sendCountdown" class="text-muted" style="display:none;"></span>--%>
        <%--            </div>--%>

        <%--            <span data-msg="email"></span>--%>
        <%--            --%>
        <%--        </div>--%>

        <div class="email-field field">

            <div class="position-relative">
                <label class="form-label" for="email">이메일</label>

                <div class="email-input-group">
                    <form:input path="email" id="email" name="email" class="form-control" placeholder="name@example.com"
                                type="text"/>
                    <button type="button" id="btnSendCode" class="btn-outline-main">인증 코드</button>
                </div>
            </div>

            <small id="verifyInfo" class="text-success"></small>
            <span data-msg="email" class="">올바른 이메일 주소를 입력해주세요.</span>
            <small id="sendCountdown"></small>
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
            <label class="form-label">비밀번호</label>
            <div class="position-relative">
                <form:input type="password" path="password" class="form-control pw-input" placeholder="영문·숫자·특수문자 8~20자"
                            id="password"/>
                <button class="password-toggle" type="button">
                    <i class="bi bi-eye"></i>
                </button>
            </div>
            <span data-msg="password"></span>
        </div>

        <!-- 닉네임 -->
        <div class="field">
            <label class="form-label">닉네임</label>
            <form:input path="nickname" cssClass="form-control" placeholder="홍이" id="nickname"/>
            <span data-msg="nickname"></span>
        </div>

        <!-- 이름 -->
        <div class="field">
            <label class="form-label">이름</label>
            <form:input path="name" cssClass="form-control" placeholder="홍길동" id="name"/>
        </div>

        <!-- 전화 -->
        <div class="field">
            <label class="form-label">전화</label>
            <form:input path="phone" cssClass="form-control" placeholder="010-1234-5678" id="phone"/>
            <span data-msg="phone"></span>
        </div>

        <!-- 생년월일 -->
        <div class="field">
            <label class="form-label">생년월일</label>
            <form:input path="birth" placeholder="yyyymmdd" type="date" cssClass="form-control" id="birth"/>
        </div>

        <!-- 성별 -->
        <div class="field">
            <label class="form-label">성별</label>
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
        urlRegister: "${urlRegister}",
        urlValEmail: "${urlValEmail}",
        urlValNick: "${urlValNick}",
        urlValPw: "${urlValPw}",
        urlValPhone: "${urlValPhone}",
        urlSendCode: "${urlSendCode}",
        urlVerifyCode: "${urlVerifyCode}",
        urlLogin: "${urlLogin}"

    };
    document.addEventListener("input", e => {
        if (e.target.type === "date") {
            e.target.classList.toggle("empty", !e.target.value);
        }
    });
</script>
<script src="/js/register.js"></script>
</body>
</html>
