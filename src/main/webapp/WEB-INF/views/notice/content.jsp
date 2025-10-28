<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<jsp:include page="/WEB-INF/views/common/header.jsp" />

<link rel="stylesheet" href="/css/notice.css">

<section id="notice" class="container sub">
    <h3>공지사항</h3>

    <div class="card">
        <div class="card-header">
            <h4><c:out value="${notice.title}"/></h4>
        </div>

        <div class="card-body">
            <p class="text-muted">
                작성자: <c:out value="${nickname}"/> |
                작성일:
                <c:choose>
                    <c:when test="${not empty notice.createdAtAsDate}">
                        <fmt:formatDate value="${notice.createdAtAsDate}" pattern="yyyy-MM-dd HH:mm"/>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${notice.createdAt}"/>
                    </c:otherwise>
                </c:choose>
            </p>

            <hr/>

            <div class="mt-3" style="min-height:200px; white-space:pre-line;">
                <!-- HTML 주입 방지: 필요시 escapeXml=false 로 변경 -->
                <c:out value="${notice.content}" />
            </div>
        </div>
    </div>

    <div class="mt-4 text-end">
        <a href="/notice/list" class="btb-main">목록으로</a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />
