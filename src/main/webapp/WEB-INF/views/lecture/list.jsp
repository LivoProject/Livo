<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/main.css" />

<main id="main">
  <section id="sub" class="container mt-4">
    <h3>강좌 검색</h3>

    <%-- 검색바 --%>
      <div class="p-3 rounded-3 mb-4" style="background: var(--color-main);">
          <form action="/lecture/search" method="get"
                class="d-flex flex-nowrap align-items-center justify-content-between gap-2 w-100">

          <%-- 주제 선택 --%>
          <select id="mainCategory" name="mainCategory" class="form-select w-auto">
              <option value="">주제</option>
              <option value="1" <c:if test="${param.mainCategory == '1'}">selected</c:if>>IT</option>
              <option value="2" <c:if test="${param.mainCategory == '2'}">selected</c:if>>자기계발</option>
              <option value="3" <c:if test="${param.mainCategory == '3'}">selected</c:if>>문화여가</option>
              <option value="4" <c:if test="${param.mainCategory == '4'}">selected</c:if>>건강</option>
              <option value="5" <c:if test="${param.mainCategory == '5'}">selected</c:if>>언어</option>
              <option value="6" <c:if test="${param.mainCategory == '6'}">selected</c:if>>인문사회</option>
              <option value="7" <c:if test="${param.mainCategory == '7'}">selected</c:if>>자격증</option>
              <option value="8" <c:if test="${param.mainCategory == '8'}">selected</c:if>>경제</option>
          </select>

        <%-- 세부분류 --%>
        <select id="subCategory" name="subCategory" class="form-select w-auto">
          <option value="">세부분류</option>
        </select>

        <%-- 검색창 --%>
            <input type="search" name="keyword" class="form-control flex-grow-1"
                   placeholder="강좌명 검색" />
        <button type="submit" class="btn btn-warning px-4 py-2" style="white-space: nowrap;">검색</button>
      </form>
    </div>

    <%-- 강좌 리스트 --%>
    <div id="recommend">
      <div class="container">

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
    
<c:set var="page" value="${lecturePage}" />
      <%@ include file="/WEB-INF/views/common/pagination.jsp" %>
  </section>

  <script src="/js/lectureList.js"></script>
</main>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
