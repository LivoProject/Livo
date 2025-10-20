<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!-- Footer -->
        <footer>
            <div>
              <p>LiVO ⓒ 2025 All Rights Reserved.</p>
            </div>
      </footer>
    </div>
    <!-- 컨텐츠 끝 -->


<c:if test="${BGM_ALLOWED}">git l
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



  <script src="<c:url value='/js/bgm-inline.js'/>?v=1"></script>
</c:if>


  </body>
</html>
