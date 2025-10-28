<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
  <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>

  <div class="container-fluid py-4 px-5">
    <div class="row">
      <div class="col-12">
        <!-- 상단 액션 -->
        <div class="d-flex justify-content-end mb-3">
          <a href="<c:url value='/admin/notice/new'/>" class="btn btn-success">새 공지사항 등록</a>
        </div>

        <!-- 검색 -->
        <form class="row g-2 mb-4 flex-wrap" method="get" action="<c:url value='/admin/notice/list'/>">
          <div class="col d-flex align-items-center">
            <input type="text"
                   class="form-control"
                   name="q"
                   value="${q}"
                   placeholder="키워드 검색..." />
          </div>
          <div class="col-auto">
            <button class="btn btn-primary" type="submit">검색</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 목록 카드 -->
    <div class="card mb-4">
      <div class="card-header pb-0">
        <h5 class="mb-0">공지사항</h5>
      </div>

      <div class="card-body px-0 pt-0 pb-2">
        <div class="table-responsive p-0">
          <table class="table align-items-center mb-0">
            <thead class="table-light text-center">
              <tr>
                <th style="width: 80px;">번호</th>
                <th style="width: 30%;">제목</th>
                <th>내용</th>
                <th style="width: 180px;">관리</th>
              </tr>
            </thead>
            <tbody id="noticeBody">

            </tbody>
          </table>
        </div>
      </div>
    </div>
      <div class="d-flex justify-content-center mt-4">
          <nav>
              <ul id="pagination" class="pagination justify-content-center"></ul>
          </nav>
      </div>
  </div>
</main>
</div>
<script src="/js/admin/notice.js"></script>
</body>
</html>
