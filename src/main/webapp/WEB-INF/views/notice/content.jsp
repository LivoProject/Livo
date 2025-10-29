<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<link rel="stylesheet" href="/css/notice.css">

<section id="noticeDetail" class="container sub">
    <div class="notice-detail-wrap">
        <div class="write-info">
            <h4><c:out value="${notice.title}"/></h4>
            <div class="write-info-small">

                <p class="text-muted">
                    <c:out value="${nickname}"/>

                </p>
                <p>
                    <c:choose>
                        <c:when test="${not empty notice.createdAtAsDate}">
                            <fmt:formatDate value="${notice.createdAtAsDate}" pattern="yyyy-MM-dd"/>
                        </c:when>
                    </c:choose>
                </p>
            </div>
        </div>

        <div class="content-area">
            <!-- HTML 주입 방지: 필요시 escapeXml=false 로 변경 -->
            <c:out value="${notice.content}" escapeXml="false"/>
        </div>
    </div>

    <div class="mt-4 text-end">
        <a href="/notice/list" class="btn-main">목록으로</a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
