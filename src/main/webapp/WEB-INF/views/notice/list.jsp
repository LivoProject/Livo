<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/notice.css">

<section id="notice" class="container sub">
    <h3>공지사항</h3>

    <table class="">
        <thead>
        <tr>
            <th class="text-center" style="width:10%">번호</th>
            <th style="width:60%">제목</th>
            <th class="text-center" style="width:15%">작성자</th>
            <th class="text-center" style="width:15%">작성일</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="notice" items="${notices}">
            <tr onclick="location.href='/notice/content?id=${notice.id}'" style="cursor:pointer;">
                <td class="text-center">${notice.id}</td>
                <td>${notice.title}</td>
                <td class="text-center">${notice.writer}</td>
                <td class="text-center">
                    <fmt:formatDate value="${notice.createdAt}" pattern="yyyy-MM-dd"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty notices}">
        <p class="text-center text-muted mt-4">등록된 공지사항이 없습니다.</p>
    </c:if>

</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
