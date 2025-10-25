<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
</head>
<body>
<h2>내 결제 내역</h2>

<table border="1" cellpadding="10">
    <tr>
        <th>강의명</th>
        <th>금액</th>
        <th>상태</th>
        <th>결제일시</th>
        <th>취소</th>
    </tr>

    <c:forEach var="p" items="${payments}">
        <tr>
            <td>${p.orderName}</td>
            <td>${p.amount}</td>
            <td>${p.status}</td>
            <td>${p.approvedAt}</td>
            <td>
                <c:if test="${p.status eq 'SUCCESS'}">
                    <button onclick="cancelPayment('${p.paymentKey}')">환불</button>
                </c:if>
            </td>
        </tr>
    </c:forEach>
</table>

<script>
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    function cancelPayment(paymentKey) {
        if (!confirm("정말 환불 하시겠어요?")) return;

        fetch("/payment/cancel", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                [csrfHeader]: csrfToken
            },
            body: "paymentKey=" + paymentKey
        }).then(res => res.json())
            .then(data => {
                if (data.status === "SUCCESS") {
                    alert("환불 완료!");
                    location.reload();
                } else {
                    alert("환불 실패: " + data.error);
                }
            });
    }
</script>
</body>
</html>
