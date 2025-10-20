<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>공지사항 등록</title>
    <link rel="stylesheet" href="<c:url value='/css/admin/notice-form.css?v=1'/>">
</head>
<body>
<div class="admin-wrap">
    <div class="toolbar">
        <div class="left">
            <h1 class="page-title">공지사항 등록</h1>
            <div class="breadcrumb">관리자 &gt; 공지사항 관리 &gt; 등록</div>
        </div>
        <div class="right">
            <!-- 리스트는 /admin/notice/list 로 이동 -->
            <a class="btn btn-ghost" href="<c:url value='/admin/notice/list'/>">목록</a>
        </div>
    </div>

    <!-- 서버에서 내려준 오류 메시지 표시 (선택) -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert" style="margin: 0 0 12px 0;">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <div class="card">
        <div class="card-body">
            <form id="noticeForm" method="post" action="<c:url value='/admin/notice'/>">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="form-row">
                    <label for="title">제목</label>
                    <div class="field">
                        <div class="counter"><span id="titleCount">0</span>/200</div>
                        <input type="text" id="title" name="title" maxlength="200"
                               placeholder="제목을 입력하세요" required>
                        <p class="muted">최대 200자</p>
                    </div>
                </div>

                <div class="form-row">
                    <label for="content">내용</label>
                    <div class="field">
                        <textarea id="content" name="content" rows="14"
                                  placeholder="공지 내용을 입력하세요" required></textarea>
                    </div>
                </div>

                <div class="form-row">
                    <label for="pinned">상단 고정</label>
                    <div class="field">
                        <label class="switch">
                            <input type="checkbox" id="pinned" name="pinned">
                            <span class="switch-ui" aria-hidden="true"></span>
                            <span class="switch-label">목록 상단에 고정</span>
                        </label>
                        <p class="muted">중요 공지는 체크하여 항상 상단에 노출합니다.</p>
                    </div>
                </div>

                <div class="actions">
                    <button type="button" class="btn" id="btnPreview">미리보기</button>
                    <div class="spacer"></div>
                    <!-- 취소도 리스트로 -->
                    <a class="btn" href="<c:url value='/admin/notice/list'/>">취소</a>
                    <button type="submit" class="btn btn-green">등록</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- 미리보기 모달 -->
<div id="previewLayer" class="modal hidden" role="dialog" aria-modal="true" aria-labelledby="pvTitle">
    <div class="modal-content">
        <div class="modal-header">
            <strong>공지 미리보기</strong>
            <button type="button" class="btn btn-ghost" id="btnClosePreview">닫기</button>
        </div>
        <div class="modal-body">
            <h2 id="pvTitle" class="pv-title"></h2>
            <div id="pvPinned" class="pv-pinned">[상단 고정]</div>
            <div id="pvContent" class="pv-content"></div>
        </div>
    </div>
</div>

<script src="<c:url value='/js/notice-form.js?v=1'/>"></script>
</body>
</html>
