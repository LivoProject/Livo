<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/main.css" />
<link rel="stylesheet" href="/css/paymentSuccess.css" />
<section id="sub" class="container" style="margin-top: 100px;">
<div class="success-container">

    <div class="success-icon">
        ✅
    </div>

    <h3 class="mb-4">결제를 완료했어요</h3>

    <div class="text-start mb-4">

        <div class="info-row">
            <span>결제금액</span>
            <span><c:out value="${result.amount}"/>원</span>
        </div>

        <div class="info-row">
            <span>주문번호</span>
            <span><c:out value="${result.orderId}"/></span>
        </div>

        <div class="info-row">
            <span>paymentKey</span>
            <span><c:out value="${result.paymentKey}"/></span>
        </div>
    </div>

    <div class="d-flex justify-content-center gap-3">
        <a href="/mypage/lecture" class="btn btn-primary">내 강의 보기</a>
        <a href="/" class="btn btn-outline-secondary">홈으로</a>
    </div>

</div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>

