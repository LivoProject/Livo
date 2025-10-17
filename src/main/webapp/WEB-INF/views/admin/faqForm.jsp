<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
        <!-- 메인 콘텐츠 -->
        <div class="container-fluid py-4 px-5">
            <div class="form-section">
                <h5 class="mb-4 fw-bold">FAQ 등록</h5>

                <form action="/admin/faq/save" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <div class="mb-3">
                        <label for="question" class="form-label">질문</label>
                        <textarea class="form-control" id="question" name="question" rows="3" required></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="answer" class="form-label">답변</label>
                        <textarea class="form-control" id="answer" name="answer" rows="5" required></textarea>
                    </div>
                    <div class="btn-group">
                        <button type="submit" class="btn btn-primary w-sm-100" style="max-width: 200px;">등록하기</button>
                        <button type="reset" class="btn btn-primary w-sm-100" style="max-width: 200px;">초기화</button>
                    </div>
                </form>
            </div>
        </div>