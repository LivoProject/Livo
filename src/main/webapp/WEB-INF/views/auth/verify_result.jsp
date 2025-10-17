<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>이메일 인증 결과</title>
</head>
<body>

<!-- ✅ 동적 include -->

<section class="verify-result container">
    <div class="content-box">
        <c:choose>
            <c:when test="${verified}">
                <h2>이메일 인증이 완료되었습니다 ✅</h2>
                <p><strong>${verifiedEmail}</strong> 이제 로그인하실 수 있습니다.</p>
                <a href="<c:url value='/auth/login'/>" class="btn btn-primary">로그인하러 가기</a>
            </c:when>
            <c:otherwise>
                <h2>이메일 인증에 실패했습니다 ❌</h2>
                <p>${error}</p>
                <a href="<c:url value='/auth/register'/>" class="btn btn-secondary">회원가입 다시 하기</a>
            </c:otherwise>
        </c:choose>
    </div>
</section>

</body>
</html>
