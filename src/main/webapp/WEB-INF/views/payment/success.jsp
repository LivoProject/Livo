<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/main.css" />
<link rel="stylesheet" href="/css/paymentSuccess.css" />

<section class="container" style="margin-top: 100px;">
    <div class="success-container">

        <div class="success-icon">✅</div>
        <h3 class="mb-4">결제가 완료되었습니다</h3>

        <div class="summary-box">
            <div class="info-row">
                <span>강의명</span>
                <span>${result.orderName}</span>
            </div>

            <div class="info-row">
                <span>결제금액</span>
                <span>${result.amount}원</span>
            </div>

            <div class="info-row">
                <span>주문번호</span>
                <span>${result.orderId}</span>
            </div>

            <div class="info-row">
                <span>결제일시</span>
                <span>${result.approvedAt}</span>
            </div>
        </div>

        <div class="btn-group d-flex justify-content-center mt-4">
            <a href="/mypage/lecture" class="btn btn-primary">내 강의 보러가기</a>
            <a href="/" class="btn btn-outline-secondary">홈으로</a>
        </div>

    </div>
</section>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>

