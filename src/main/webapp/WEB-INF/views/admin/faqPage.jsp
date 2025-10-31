<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <!-- 메인 콘텐츠 -->
        <div class="container-fluid py-4 px-5">
          <div class="row">
            <div class="col-12">
              <div class="text-end mb-3">
                  <a href="faq/insert" class="btn btn-success">새 FAQ 등록</a>
              </div>
              <div class="card mb-4 vertical-scroll-wrap">
                <div class="card-header pb-0">
                  <h5>FAQ</h5>
                </div>
                <div class="card-body px-0 pt-0 pb-2 vertical-scroll">
                  <div class="table-responsive p-0">
                    <table class="table align-items-center mb-0">
                      <thead class="table-light text-center">
                        <tr>
                            <th>번호</th>
                            <th>질문</th>
                            <th>답변</th>
                            <th class="text-center">관리</th>
                        </tr>
                      </thead>
                      <tbody id="faqTableBody">
<%--                          <c:forEach var="faq" items="${faq}" varStatus="status">--%>
<%--                              <tr class="text-center">--%>
<%--                                  <td>${status.index + 1}</td>--%>
<%--                                  <td class="text-start">${faq.question}</td>--%>
<%--                                  <td class="text-start">--%>
<%--                                    <c:choose>--%>
<%--                                        <c:when test="${fn:length(faq.answer) > 70}">--%>
<%--                                          ${fn:substring(faq.answer, 0, 70)}...--%>
<%--                                        </c:when>--%>
<%--                                        <c:otherwise>--%>
<%--                                          ${faq.answer}--%>
<%--                                        </c:otherwise>--%>
<%--                                      </c:choose>--%>
<%--                                  </td>--%>
<%--                                  <td class="text-center">--%>
<%--                                      <div class="d-flex justify-content-center gap-2">--%>
<%--                                          <a href="/admin/faq/edit?id=${faq.id}" class="btn btn-sm btn-primary me-1">수정</a>--%>
<%--                                          <form action="/admin/faq/delete" method="post">--%>
<%--                                              <input type="hidden" name="id" value="${faq.id}">--%>
<%--                                              <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('정말 삭제하시겠습니까?');">삭제</button>--%>
<%--                                          </form>--%>
<%--                                      </div>--%>

<%--                                  </td>--%>
<%--                              </tr>--%>
<%--                          </c:forEach>--%>
                        </tbody>
                    </table>
                  </div>

                </div>
              </div>
                <ul id="pagination" class="pagination justify-content-center"></ul>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
<script src="/js/admin/faq.js"></script>
</body>

</html>
