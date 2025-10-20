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


<c:if test="${BGM_ALLOWED}">
  <script>
    window.__BGM_ALLOWED__ = true;
    // 컨텍스트패스가 있다면 같이 내려주세요 (없으면 지워도 됨)
    // window.__CTX__ = '${pageContext.request.contextPath}';
  </script>
  <script src="<c:url value='/js/bgm.js'/>?v=12"></script>
</c:if>
  </body>
</html>
