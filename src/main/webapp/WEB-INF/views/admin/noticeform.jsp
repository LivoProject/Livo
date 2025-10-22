<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title><c:out value="${empty n ? '공지사항 등록' : '공지사항 수정'}"/></title>
    <link rel="stylesheet" href="<c:url value='/css/admin/notice-form.css?v=1'/>">
</head>
<body>
<div class="admin-wrap">
    <div class="toolbar">
        <div class="left">
            <h1 class="page-title"><c:out value="${empty n ? '공지사항 등록' : '공지사항 수정'}"/></h1>
            <div class="breadcrumb">
                관리자 &gt; 공지사항 관리 &gt; <c:out value="${empty n ? '등록' : '수정'}"/>
            </div>
        </div>
        <div class="right">
            <a class="btn btn-ghost" href="<c:url value='/admin/notice/list'/>">목록</a>
        </div>
    </div>


    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert" style="margin: 0 0 12px 0;">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <div class="card">
        <div class="card-body">

            <c:choose>
                <%-- 수정 모드 --%>
                <c:when test="${not empty n}">
                    <form id="noticeForm" method="post" action="<c:url value='/admin/notice/${n.id}'/>">
                        <!-- PUT 메서드 스위치 & CSRF -->
                        <input type="hidden" name="_method" value="PUT"/>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                        <div class="form-row">
                            <label for="title">제목</label>
                            <div class="field">
                                <div class="counter"><span id="titleCount">0</span>/200</div>
                                <input type="text" id="title" name="title" maxlength="200"
                                       value="<c:out value='${n.title}'/>"
                                       placeholder="제목을 입력하세요" required>
                                <p class="muted">최대 200자</p>
                            </div>
                        </div>

                        <div class="form-row">
                            <label for="content">내용</label>
                            <div class="field">
                                <textarea id="content" name="content" rows="14"
                                          placeholder="공지 내용을 입력하세요" required><c:out value="${n.content}"/></textarea>
                            </div>
                        </div>

                        <div class="form-row">
                            <label for="pinned">상단 고정</label>
                            <div class="field">
                                <label class="switch">
                                    <input type="checkbox" id="pinned" name="pinned" <c:if test="${n.pinned}">checked</c:if>>
                                    <span class="switch-ui" aria-hidden="true"></span>
                                    <span class="switch-label">목록 상단에 고정</span>
                                </label>
                                <p class="muted">중요 공지는 체크하여 항상 상단에 노출합니다.</p>
                            </div>
                        </div>

                        <div class="form-row">
                            <label for="visible">노출</label>
                            <div class="field">
                                <label class="switch">
                                    <input type="checkbox" id="visible" name="visible" <c:if test="${n.visible}">checked</c:if>>
                                    <span class="switch-ui" aria-hidden="true"></span>
                                    <span class="switch-label">사용자에게 노출</span>
                                </label>
                            </div>
                        </div>

                        <div class="actions">
                            <button type="button" class="btn" id="btnPreview">미리보기</button>
                            <div class="spacer"></div>
                            <a class="btn" href="<c:url value='/admin/notice/list'/>">취소</a>
                            <button type="submit" class="btn btn-green">수정 저장</button>
                        </div>
                    </form>
                </c:when>

                <%-- 등록 모드 --%>
                <c:otherwise>
                    <form id="noticeForm" method="post" action="<c:url value='/admin/notice'/>">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                        <div class="form-row">
                            <label for="title">제목</label>
                            <div class="field">
                                <div class="counter"><span id="titleCount">0</span>/200</div>
                                <input type="text" id="title" name="title" maxlength="200"
                                       value=""
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

                        <div class="form-row">
                            <label for="visible">노출</label>
                            <div class="field">
                                <label class="switch">
                                    <input type="checkbox" id="visible" name="visible" checked>
                                    <span class="switch-ui" aria-hidden="true"></span>
                                    <span class="switch-label">사용자에게 노출</span>
                                </label>
                            </div>
                        </div>

                        <div class="actions">
                            <button type="button" class="btn" id="btnPreview">미리보기</button>
                            <div class="spacer"></div>
                            <a class="btn" href="<c:url value='/admin/notice/list'/>">취소</a>
                            <button type="submit" class="btn btn-green">등록</button>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>

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

<<<<<<< HEAD
<script src="<c:url value='/js/notice-form.js?v=1'/>"></script>
<script>
    // 제목 글자수 초기화(수정 모드에서 서버 값 기준)
    (function(){
        var t = document.getElementById('title');
        if (!t) return;
        var c = document.getElementById('titleCount');
        var update = function(){ c.textContent = (t.value || '').length; };
        t.addEventListener('input', update);
        update();
    })();
</script>
=======
<script src="<c:url value='/js/admin/notice-form.js?v=1'/>"></script>
>>>>>>> main
</body>
</html>
