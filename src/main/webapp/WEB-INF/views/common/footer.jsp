<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    img {
        width: 100%;
        height: 100%;
    }

    .chat-fab {
        position: fixed;
        right: 24px; /* 화면 우측 여백 */
        bottom: 24px; /* 화면 하단 여백 (footer 위로) */
        width: 60px;
        height: 60px;
        border-radius: 50%;
        border: none;
        outline: none;
        cursor: pointer;
        z-index: 2147483647; /* 항상 맨 위로 */
        display: flex;
        align-items: center;
        justify-content: center;
        background: #0d6efd; /* Bootstrap primary */
        color: #fff;
        box-shadow: 0 10px 20px rgba(13, 110, 253, .35);
        transition: transform .15s ease, box-shadow .15s ease, background .15s ease;
    }

    .chat-fab:hover {
        transform: translateY(-1px);
        box-shadow: 0 14px 28px rgba(13, 110, 253, .45);
    }

    .chat-fab:active {
        transform: translateY(1px) scale(.98);
    }

    .chat-fab i {
        font-size: 26px; /* 아이콘 크기 */
        line-height: 1;
    }

    .chat-panel {
        position: fixed;
        right: 24px;
        bottom: 96px; /* FAB 위에 뜨도록 */
        width: min(360px, 92vw);
        height: 480px;
        background: #fff;
        border-radius: 16px;
        box-shadow: 0 18px 50px rgba(0, 0, 0, .2);
        z-index: 2147483646;
        overflow: hidden;
        display: flex;
        flex-direction: column;
    }

    .chat-panel-header {
        padding: 12px 16px;
        background: #0d6efd;
        color: #fff;
        font-weight: 600;
    }

    .chat-panel-body {
        padding: 16px;
        overflow: auto;
        flex: 1;
    }


</style>

<!-- Footer -->
<button id="chat-fab" class="chat-fab" aria-label="챗봇 열기" title="챗봇">
    <img src="/img/common/chat_icon01.svg" alt="챗봇">
</button>

<!-- 간단한 패널 -->
<div id="chat-panel" class="chat-panel" hidden>
    <!-- 원하는 내용 삽입 -->
    <div class="chat-panel-header">LiVO 챗봇</div>
    <div class="chat-panel-body">무엇을 도와드릴까요?</div>
</div>

<footer>
    <div>
        <p>LiVO ⓒ 2025 All Rights Reserved.</p>
    </div>
    <!-- 컨텐츠 끝 -->


    <c:if test="${BGM_ALLOWED}">
        <link rel="stylesheet" href="<c:url value='/css/bgm-toggle.css'/>?v=2"/>
        <link rel="preload" href="<c:url value='/audio/login_success.mp3'/>" as="audio" type="audio/mpeg"/>

        <div id="bgmToggle"
             class="bgm-toggle bgm-off"
             role="button"
             aria-pressed="false"
             aria-label="배경음악 끔"
             title="배경음악 켜기/끄기">
            <span class="bgm-dot" aria-hidden="true"></span>
            <span class="bgm-label" aria-hidden="true">🔇</span>
        </div>


    </c:if>

</footer>

<script src="<c:url value='/js/bgm-inline.js'/>?v=1"></script>
<script>
    const fab = document.getElementById('chat-fab');
    const panel = document.getElementById('chat-panel');
    if (fab && panel) {
        fab.addEventListener('click', () => {
            panel.hidden = !panel.hidden;
            fab.setAttribute('aria-expanded', String(!panel.hidden));
        });
    }
</script>
</body>

</body>
</html>
