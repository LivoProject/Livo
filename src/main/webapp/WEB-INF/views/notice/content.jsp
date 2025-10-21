<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/notice.css">

<section id="notice" class="container sub">
    <h3>공지사항</h3>
    <div class="card shadow-sm">
        <div class="card-header">
            <h4>${notice.title}</h4>
        </div>
        <div class="card-body">
            <p class="text-muted small mb-1">
                작성자: ${notice.writer} | 작성일: <fmt:formatDate value="${notice.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
            </p>
            <hr/>
            <div class="mt-3" style="min-height:200px; white-space:pre-line;">
                ${notice.content}
            </div>
        </div>
    </div>

    <div class="mt-4 text-end">
        <a href="/notice/list" class="btb-main">목록으로</a>
    </div>
</section>


<%@ include file="/WEB-INF/views/common/footer.jsp" %>
