<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>
<link rel="stylesheet" href="/css/main.css" />

<main id="main">
  <section id="sub" class="container mt-4">
    <h3>강좌 검색</h3>

    <%-- 검색바 --%>
    <div class="p-3 text-bg-dark rounded-3 mb-4">
      <form action="/lecture/search" method="get"
            class="d-flex flex-nowrap align-items-center justify-content-center gap-2">

        <%-- 주제 선택 --%>
        <select id="mainCategory" name="mainCategory" class="form-select w-auto">
          <option value="">주제</option>
          <option value="1">IT</option>
          <option value="2">자기계발</option>
          <option value="3">문화여가</option>
          <option value="4">건강</option>
          <option value="5">언어</option>
          <option value="6">인문사회</option>
          <option value="7">자격증</option>
          <option value="8">경제</option>
        </select>

        <%-- 세부분류 --%>
        <select id="subCategory" name="subCategory" class="form-select w-auto">
          <option value="">세부분류</option>
        </select>

        <%-- 검색창 --%>
        <input type="search" name="keyword" class="form-control w-25" placeholder="강좌명 검색" />
        <button type="submit" class="btn btn-warning px-4 py-2" style="white-space: nowrap;">검색</button>
      </form>
    </div>

    <%-- 강좌 리스트 --%>
    <div id="recommend">
      <div class="container">
        <h3>전체 강좌</h3>
        <div class="recommend-grid">

          <c:choose>
            <%-- 검색 결과가 있을 때 --%>
            <c:when test="${not empty lectures}">
              <c:forEach var="lecture" items="${lectures}">
                <a href="/lecture/content/${lecture.lectureId}" class="card popular-card">
                  <!-- 썸네일 -->
                  <div class="card-thumb" style="height: 200px; border-radius: 12px 12px 0 0; overflow: hidden;">
                    <img src="${lecture.thumbnailUrl}"
                         onerror="this.src='/img/common/no-image.png';"
                         alt="lecture thumbnail"
                         class="img-fluid rounded shadow-sm border"
                         style="max-height: 280px; object-fit: cover;">
                  </div>

                  <%-- 강좌정보 --%>
                  <div class="card-body">
                    <h6>${lecture.title}</h6>
                    <p>${lecture.tutorName} ∣ <fmt:formatNumber value="${lecture.price}" type="number"/></p>
                    <div class="card-review">
                      <div>
                        <span>⭐ <fmt:formatNumber value="${avgStarMap[lecture.lectureId]}" type="number" maxFractionDigits="1" /></span>
                        <span>(${reviewCountMap[lecture.lectureId]})</span>
                      </div>
                      <div>
                        <i class="bi bi-person-fill"></i>
                        <span>${lecture.reservationCount}</span>
                      </div>
                    </div>
                  </div>
                </a>
              </c:forEach>
            </c:when>

            <%-- 검색 결과가 없을 때 --%>
            <c:otherwise>
              <div class="text-center p-5 w-100">
                <h5>검색 결과가 없습니다.</h5>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>

    <%-- ✅ 서버 기본 페이지네이션 + JS가 이후 덮어쓰기 가능 --%>
    <nav aria-label="Page navigation">
      <ul class="pagination justify-content-center mt-4">
        <c:if test="${!lecturePage.first}">
          <li class="page-item">
            <a class="page-link" href="?page=${lecturePage.number - 1}&keyword=${keyword}">이전</a>
          </li>
        </c:if>

        <c:if test="${lecturePage.totalPages > 0}">
          <c:forEach var="i" begin="0" end="${lecturePage.totalPages - 1}">
            <li class="page-item ${lecturePage.number == i ? 'active' : ''}">
              <a class="page-link" href="?page=${i}&keyword=${keyword}">${i + 1}</a>
            </li>
          </c:forEach>
        </c:if>

        <c:if test="${!lecturePage.last}">
          <li class="page-item">
            <a class="page-link" href="?page=${lecturePage.number + 1}&keyword=${keyword}">다음</a>
          </li>
        </c:if>
      </ul>
    </nav>

  </section>

  <script src="/js/lectureList.js"></script>
</main>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
