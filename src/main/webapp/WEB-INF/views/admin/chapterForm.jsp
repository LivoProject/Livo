<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>

    <div class="container-fluid py-4 px-5">
        <div class="form-section">
            <h5 class="mb-4 fw-bold">2단계: 챕터 등록</h5>

<%--            <input type="hidden" id="lectureId" value="${lectureId}" />--%>

            <div id="chapterContainer" class="sortable">
                <div class="chapter border rounded p-3 mb-3">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <strong>Chapter <span class="chapter-index">1</span></strong>
                        <span class="text-muted small">(드래그하여 순서 변경 가능)</span>
                    </div>

                    <label>챕터명</label>
                    <input type="text" class="form-control mt-2 mb-2 chapterName" />

                    <input type="hidden" class="chapterOrder" value="1" />

                    <label>유튜브 URL</label>
                    <input type="text" class="form-control mt-2 mb-2 youtubeUrl" />

                    <label>내용</label>
                    <textarea class="form-control chapterContent mt-2" rows="3"></textarea>
                </div>
            </div>

            <button type="button" id="addChapterBtn" class="btn btn-outline-secondary mt-3">+ 챕터 추가</button>
            <button type="button" id="submitAllBtn" class="btn btn-primary mt-3">등록 완료</button>
        </div>
    </div>
</main>
</div>
</body>
<script src="/js/admin/chapterForm.js"></script>