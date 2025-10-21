<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>


<main class="container">

    <h1>🎨 UI 컨벤션 가이드</h1>
    <!-- 버튼 가이드 -->
    <div style="margin-top: 100px;">
        <div class="mb-3">
            <button type="button" class="btn-main">기본 버튼</button>
            <button type="button" class="btn-cancel">취소 버튼</button>
            <button type="button" class="btn-point">강조 버튼</button>
        </div>

    </div>

    <!-- 모달 가이드 -->
    <div>
        <!-- 모달 열기 버튼 -->
        <button type="button" class="btn-outline-main" data-bs-toggle="modal" data-bs-target="#exampleModal">
            모달 열기
        </button>
        <!-- //모달 열기 -->

        <!-- 모달 -->
        <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">공통 모달 제목</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
                    </div>
                    <div class="modal-body">
                        이곳은 모달 내용입니다.<br>
                        설명이나 폼, 알림 메시지 등을 넣을 수 있습니다.
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" data-bs-dismiss="modal">취소</button>
                        <button type="button" class="btn-main">확인</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- // 모달 -->

        <!-- 제목 가이드 -->
        <h3>큰 제목</h3>
        <h4>소 제목</h4>
    </div>
</main>
</body>
</html>
