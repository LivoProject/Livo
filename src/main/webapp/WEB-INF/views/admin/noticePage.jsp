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
            <tbody>
              <!-- 비었을 때 -->
              <c:if test="${empty page || empty page.content}">
                <tr>
                  <td colspan="4" class="text-center py-4">등록된 공지사항이 없습니다.</td>
                </tr>
              </c:if>

              <!-- 목록 렌더링 -->
              <c:forEach var="n" items="${page.content}" varStatus="s">
                <tr>
                  <td class="text-center">
                    <c:out value="${page.totalElements - (page.number * page.size) - s.index}" />
                  </td>

                  <td class="text-start">
                    <c:if test="${n.pinned}">
                      <span class="badge bg-success me-1">고정</span>
                    </c:if>
                    <c:out value="${n.title}" />
                    <div class="text-muted small mt-1">
                      <c:choose>
                        <c:when test="${not empty n.createdAtAsDate}">
                          <fmt:formatDate value="${n.createdAtAsDate}" pattern="yyyy-MM-dd HH:mm"/>
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                      </c:choose>
                         <!-- 작성자 표시 수정 -->
                      · 작성자
                      <c:out value="${empty n.nickname ? '알수없음' : n.nickname}" />
                      · 조회 <c:out value="${n.viewCount}" />
                      <c:if test="${!n.visible}">
                        · <span class="text-danger">비노출</span>
                      </c:if>
                    </div>
                  </td>

                  <td class="text-start">
                    <div class="text-truncate"
                         style="-webkit-line-clamp: 2; display: -webkit-box; -webkit-box-orient: vertical; overflow: hidden; max-width: 520px;">
                      <c:out value="${n.content}" />
                    </div>
                  </td>

                  <td class="text-center">
                    <!-- noticeId -> id 로 변경 -->
                    <a href="<c:url value='/admin/notice/${n.id}/edit'/>"
                       class="btn btn-sm btn-primary me-1">수정</a>

                    <form action="<c:url value='/admin/notice/${n.id}'/>"
                          method="post"
                          style="display:inline-block;"
                          onsubmit="return confirm('정말 삭제하시겠습니까?');">
                      <input type="hidden" name="_method" value="DELETE" />
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                      <button type="submit" class="btn btn-sm btn-danger">삭제</button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>

        <!-- 페이징 -->
        <c:if test="${!empty page && page.totalPages > 1}">
          <nav class="mt-3" aria-label="Page navigation">
            <ul class="pagination justify-content-center mb-0">
              <li class="page-item ${page.first ? 'disabled' : ''}">
                <a class="page-link"
                   href="<c:url value='/admin/notice/list?page=${page.number - 1}&size=${page.size}&q=${q}'/>">이전</a>
              </li>

              <c:forEach begin="0" end="${page.totalPages - 1}" var="i">
                <li class="page-item ${i == page.number ? 'active' : ''}">
                  <a class="page-link"
                     href="<c:url value='/admin/notice/list?page=${i}&size=${page.size}&q=${q}'/>">${i + 1}</a>
                </li>
              </c:forEach>

              <li class="page-item ${page.last ? 'disabled' : ''}">
                <a class="page-link"
                   href="<c:url value='/admin/notice/list?page=${page.number + 1}&size=${page.size}&q=${q}'/>">다음</a>
              </li>
            </ul>
          </nav>
        </c:if>
      </div>
    </div>
  </div>
</main>

</body>
</html>
