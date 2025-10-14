<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8"/>
  <title>로그인</title>
  <!-- (AJAX 쓸 때만 사용) -->
  <meta name="_csrf" content="${_csrf.token}"/>
  <meta name="_csrf_header" content="${_csrf.headerName}"/>
</head>
<body>
<h2>로그인</h2>

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

<form method="post" action="<c:url value='/auth/login'/>" accept-charset="UTF-8" autocomplete="on">
  <div>
    <label for="email">Email</label><br/>
    <input id="email" type="email" name="email" required />
  </div>
  <div>
    <label for="password">Password</label><br/>
    <input id="password" type="password" name="password" required />
  </div>

  <!-- ✅ CSRF 토큰 (중요) -->
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

  <div style="margin-top:10px">
    <button type="submit">로그인</button>
  </div>
</form>

<hr/>

<a href="<c:url value='/auth/register'/>">회원가입</a> |
<a href="<c:url value='/'/>">메인으로</a>
</body>
</html>
