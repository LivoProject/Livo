<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- 페이지네이션 -->
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
