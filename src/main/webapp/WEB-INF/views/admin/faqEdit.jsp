<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<link rel="stylesheet" href="/css/admin/faq.css"/>
<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
        <!-- 메인 콘텐츠 -->
        <div class="container-fluid py-4 px-5">
            <div class="form-section">
                <h5 class="mb-4 fw-bold">FAQ 수정</h5>

                <form action="/admin/faq/edit" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <input type="hidden" name="id" value="${faq.id}"/>
                    <div class="mb-3">
                        <label for="question" class="form-label">질문</label>
                        <textarea class="form-control" id="question" name="question" rows="3" required>${faq.question}</textarea>
                    </div>
                    <div class="mb-3">
                        <label for="answer" class="form-label">답변</label>
                        <textarea class="form-control" id="answer" name="answer" rows="5" required>${faq.answer}</textarea>
                    </div>
                    <div class="btn-group">
                        <button type="submit" class="btn btn-primary w-sm-100">수정하기</button>
                        <button type="reset" class="btn btn-primary w-sm-100">초기화</button>
                    </div>
                </form>
            </div>
        </div>
</main>
</div>
<script>
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('updated') === 'true') {
        alert("수정 되었습니다.");
    }
</script>
</body>