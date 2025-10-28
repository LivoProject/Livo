<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<jsp:include page="/WEB-INF/views/common/header.jsp" />

<link rel="stylesheet" href="/css/notice.css">

<section id="notice" class="container sub">
    <h3 class="mb-4">공지사항</h3>

    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-primary text-center">
            <tr>
                <th style="width:10%">번호</th>
                <th style="width:60%">제목</th>
                <th style="width:15%">작성자</th>
                <th style="width:15%">작성일</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="notice" items="${notices}" varStatus="s">
                <tr onclick="location.href='/notice/content?id=${notice.id}'" style="cursor:pointer;">
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
    </div>

    <c:if test="${empty notices}">
        <p class="text-center text-muted mt-4">등록된 공지사항이 없습니다.</p>
    </c:if>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
