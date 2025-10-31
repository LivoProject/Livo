<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<link rel="stylesheet" href="/css/admin/notice-form.css"/>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>

    <!-- 메인 콘텐츠 -->
    <div class="container-fluid py-4 px-5">
        <div class="form-section">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h5 class="fw-bold mb-1">
                        <c:out value="${empty n ? '공지사항 등록' : '공지사항 수정'}"/>
                    </h5>
                </div>
                <div>
                    <a class="btn btn-outline-secondary btn-sm" href="<c:url value='/admin/notice'/>">목록</a>
                </div>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <div class="">
                <div class="">
                    <c:choose>

                        <%-- 수정 모드 --%>
                        <c:when test="${not empty n}">
                            <form id="noticeForm" method="post" action="<c:url value='/admin/notice/${n.id}'/>">
                                <input type="hidden" name="_method" value="PUT"/>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                                <div class="mb-3">
                                    <label for="title" class="form-label">제목</label>
                                    <div class="position-relative">
                                        <input type="text" id="title" name="title" maxlength="200"
                                               value="<c:out value='${n.title}'/>"
                                               placeholder="제목을 입력하세요" class="form-control" required>
                                        <div class="form-text text-end"><span id="titleCount">0</span>/200</div>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="content" class="form-label">내용</label>
                                    <textarea id="summernote" name="content" class="form-control" required>
                                        <c:out value="${n.content}"/>
                                    </textarea>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">상단 고정</label>
                                    <div class="form-check form-switch">
                                        <input class="form-check-input" type="checkbox" id="pinned" name="pinned"
                                               <c:if test="${n.pinned}">checked</c:if>>
                                        <label class="form-check-label" for="pinned">목록 상단에 고정</label>
                                    </div>
                                    <div class="form-text text-muted">중요 공지는 체크하여 항상 상단에 노출됩니다.</div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">노출</label>
                                    <div class="form-check form-switch">
                                        <input class="form-check-input" type="checkbox" id="visible" name="visible"
                                               <c:if test="${n.visible}">checked</c:if>>
                                        <label class="form-check-label" for="visible">사용자에게 노출</label>
                                    </div>
                                </div>

                                <div class="d-flex justify-content-end gap-2 mt-4">
                                    <button type="button" class="btn btn-outline-secondary" id="btnPreview">미리보기</button>
                                    <a class="btn btn-light" href="<c:url value='/admin/notice'/>">취소</a>
                                    <button type="submit" class="btn btn-primary">수정 저장</button>
                                </div>
                            </form>
                        </c:when>

                        <%-- 등록 모드 --%>
                        <c:otherwise>
                            <form id="noticeForm" method="post" action="<c:url value='/admin/notice'/>">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                                <div class="mb-3">
                                    <label for="title" class="form-label">제목</label>
                                    <div class="position-relative">
                                        <input type="text" id="title" name="title" maxlength="200"
                                               placeholder="제목을 입력하세요" class="form-control" required>
                                        <div class="form-text text-end"><span id="titleCount">0</span>/200</div>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="content" class="form-label">내용</label>
                                    <textarea id="summernote" name="content" class="form-control" rows="14"
                                              placeholder="공지 내용을 입력하세요" required></textarea>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">상단 고정</label>
                                    <div class="form-check form-switch">
                                        <input class="form-check-input" type="checkbox" id="pinned" name="pinned">
                                        <label class="form-check-label" for="pinned">목록 상단에 고정</label>
                                    </div>
                                    <div class="form-text text-muted">중요 공지는 체크하여 항상 상단에 노출됩니다.</div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">노출</label>
                                    <div class="form-check form-switch">
                                        <input class="form-check-input" type="checkbox" id="visible" name="visible" checked>
                                        <label class="form-check-label" for="visible">사용자에게 노출</label>
                                    </div>
                                </div>

                                <div class="d-flex justify-content-end gap-2 mt-4">
                                    <button type="button" class="btn btn-outline-secondary" id="btnPreview">미리보기</button>
                                    <a class="btn btn-light" href="<c:url value='/admin/notice'/>">취소</a>
                                    <button type="submit" class="btn btn-primary">등록</button>
                                </div>
                            </form>
                        </c:otherwise>

                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</main>
<!-- 미리보기 모달 -->
<div id="previewLayer" class="modal hidden" role="dialog" aria-modal="true" aria-labelledby="pvTitle">
    <div class="modal-content">
        <div class="modal-header">
            <strong>공지 미리보기</strong>
            <button type="button" class="btn btn-main" id="btnClosePreview">닫기</button>
        </div>
        <div class="modal-body">
            <h2 id="pvTitle" class="pv-title"></h2>
            <div id="pvPinned" class="pv-pinned">[상단 고정]</div>
            <div id="pvContent" class="pv-content"></div>
        </div>
    </div>
</div>
<link href="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-lite.min.js"></script>
<script src="/js/admin/notice-form.js"></script>
</body>
</html>
