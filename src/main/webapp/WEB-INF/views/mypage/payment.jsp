<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<%@ include file="/WEB-INF/views/common/header.jsp" %>


<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">
    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 메인 컨텐츠 -->
    <main class="main-content">
        <h3>결제 내역</h3>


        <div class="review-list large">

            <c:if test="${not empty payments}">
                <c:forEach var="payment" items="${payments}">
                    <div class="payment-card"
                         data-payment-id="${payment.paymentId}"
                         data-payment-status="${payment.status}">

                        <!-- 주문일자 -->
                        <p class="order-date">주문 날짜 <span
                                class="fw-bold">${fn:substringBefore(payment.approvedAt, 'T')}</span></p>

                        <!-- 카드 본문 -->
                        <div class="payment-box">
                            <div class="payment-header">
                                <span class="badge status-${payment.status}">${payment.status}</span>
                                <span class="order-info">주문 번호 ${payment.orderId}</span>
                            </div>

                            <div class="payment-body">
                                <p class="lecture-title">${payment.orderName}</p>
                                <p class="lecture-sub">결제 방식: ${payment.method}</p>
                            </div>

                            <div class="payment-footer">
                              <span class="amount">
                                ₩<fmt:formatNumber value="${payment.amount}" pattern="#,###"/>
                              </span>

                                <c:if test="${payment.status == 'SUCCESS'}">
                                    <button class="btn-point btn-refund"
                                            data-payment-key="${payment.paymentKey}"
                                            data-payment-id="${payment.paymentId}">
                                        환불 요청
                                    </button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>

            <c:if test="${empty payments}">
                <p class="text-muted">결제 내역이 없습니다.</p>
            </c:if>
        </div>


        <%@ include file="/WEB-INF/views/common/pagination.jsp" %>

    </main>
</section>
<!-- 컨텐츠 끝 -->

<%@ include file="/WEB-INF/views/common/modal.jsp" %>
<script src="/js/payment-refund.js"></script>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
