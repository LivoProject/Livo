<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- Footer -->

<!-- 플로팅 버튼 컨테이너 -->
<div class="floating-buttons">
    <!-- 챗봇 -->
    <button id="chat-fab" class="chat-fab" aria-label="챗봇 열기" title="챗봇">
        <img src="/img/common/chat_icon01.svg" alt="챗봇">
    </button>

    <!-- 배경음악 -->
    <c:if test="${BGM_ALLOWED}">
        <button id="bgmBtn" class="fab" title="배경음악 켜기/끄기">
            <div id="bgmToggle"
                 class="bgm-toggle bgm-off"
                 role="button"
                 aria-pressed="false"
                 aria-label="배경음악 끔"
                 title="배경음악 켜기/끄기">
                <span class="bgm-label" aria-hidden="true"></span>
                <span class="bgm-dot" aria-hidden="true"></span>
            </div>
        </button>
    </c:if>

    <!--  맨 위로 -->
    <button id="toTopBtn" title="맨 위로">
        <i class="bi bi-arrow-up"></i>
    </button>
</div>


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

    <%--
        <c:if test="${BGM_ALLOWED}">
            <link rel="stylesheet" href="/css/bgm-toggle.css"/>
            <link rel="preload" href="/audio/login_success.mp3" as="audio" type="audio/mpeg"/>

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
    --%>

</footer>

</body>

</body>
</html>
