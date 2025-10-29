<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<link rel="stylesheet" href="/css/notice.css">

<section id="notice" class="container sub">
    <h3 class="mb-4">공지사항</h3>

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

        <c:if test="${not empty pinnedNotices}">
            <c:forEach var="notice" items="${pinnedNotices}">
                <tr class="table-warning">
                <tr onclick="location.href='/notice/content?id=${notice.id}'">
                    <!-- 최신글이 위 → 최신글이 1번 -->
                    <td class="text-center">📌</td>
                    <td class="text-start">${notice.title}</td>
                    <td class="text-center">${notice.nickname}</td>
                    <td class="text-center">
                        <fmt:formatDate value="${notice.createdAtAsDate}" pattern="yyyy-MM-dd"/>
                    </td>
                </tr>
            </c:forEach>
        </c:if>


        <c:forEach var="notice" items="${notices}" varStatus="s">
            <tr onclick="location.href='/notice/content?id=${notice.id}'">
                <!-- 최신글이 위 → 최신글이 1번 -->
                <td class="text-center">${s.index + 1}</td>
                <td class="text-start">${notice.title}</td>
                <td class="text-center">${notice.nickname}</td>
                <td class="text-center">
                    <fmt:formatDate value="${notice.createdAtAsDate}" pattern="yyyy-MM-dd"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty notices}">
        <p class="text-center text-muted mt-4">등록된 공지사항이 없습니다.</p>
    </c:if>

    <nav class="pagination-wrap">
        <c:if test="${page != null and page.totalPages > 0}">
            <ul class="pagination">
                <c:if test="${not page.first}">
                    <li class="page-item">
                        <a class="page-link" href="?page=${page.number - 1}"> <i class="bi bi-chevron-left"></i></a>
                    </li>
                </c:if>

                <c:forEach var="i" begin="0" end="${page.totalPages - 1}">
                    <li class="page-item ${page.number == i ? 'active' : ''}">
                        <a class="page-link" href="?page=${i}">${i + 1}</a>
                    </li>
                </c:forEach>

                <c:if test="${not page.last}">
                    <li class="page-item next">
                        <a class="page-link" href="?page=${page.number + 1}"> <i class="bi bi-chevron-right"></i></a>
                    </li>
                </c:if>
            </ul>
        </c:if>
    </nav>

</section>


<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
