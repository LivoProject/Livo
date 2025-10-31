<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/main.css"/>

<main id="main">
    <section class="container sub">
        <h3>강좌 검색</h3>

        <%-- 검색바 --%>
        <div class="p-3 rounded-3 mb-4" style="background: var(--color-main);">
            <form id="searchForm" class="d-flex flex-nowrap align-items-center justify-content-between gap-2 w-100">

                <%-- 주제 선택 --%>
                <select id="mainCategory" name="mainCategory" class="form-select w-auto">
                    <option value="">주제</option>
                    <option value="1" <c:if test="${param.mainCategory == '1'}">selected</c:if>>IT</option>
                    <option value="2" <c:if test="${param.mainCategory == '2'}">selected</c:if>>자기개발</option>
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
                       placeholder="강좌명 검색"/>
                <button type="submit" class="btn btn-warning py-2" style="white-space: nowrap;">검색</button>
            </form>
        </div>

        <%-- 강좌 리스트 --%>
        <div id="recommend">
            <div class="container">
                <%-- 필터 --%>
                <div class="d-flex align-items-center justify-content-end mb-3">
                </div>

                <div class="recommend-grid">

                    <c:choose>
                        <%-- 검색 결과가 있을 때 --%>
                        <c:when test="${not empty lectures}">
                            <c:forEach var="lecture" items="${lectures}">
                                <a href="/lecture/content/${lecture.lectureId}" class="card popular-card">
                                    <!-- 썸네일 -->
                                    <div class="card-thumb"
                                         style="height: 180px; border-radius: 12px 12px 0 0; overflow: hidden;">
                                        <img src="${lecture.thumbnailUrl}"
                                             onerror="this.src='/img/common/no-image.png';"
                                             alt="lecture thumbnail"
                                             class="img-fluid rounded shadow-sm border"
                                             style="height: 100%; object-fit: cover;">
                                    </div>

                                        <%-- 강좌정보 --%>
                                    <div class="card-body justify-content-between" style="gap: 0;">
                                        <div class="d-flex align-items-center justify-content-between mb-2">
                                            <h6 class="fw-bold text-ellipsis-2 mb-0 flex-grow-1 lh-base">
                                                    ${lecture.title}
                                                <c:choose>
                                                    <c:when test="${lecture.status == 'CLOSED' || lecture.status == 'ENDED'}">
                                                        <button type="button" class="badge bg-secondary flex-shrink-0"
                                                                disabled
                                                                style="width: max-content">
                                                            <c:choose>
                                                                <c:when test="${lecture.status == 'CLOSED'}">예약 마감</c:when>
                                                                <c:when test="${lecture.status == 'ENDED'}">강의 종료</c:when>
                                                            </c:choose>
                                                        </button>
                                                    </c:when>

                                                    <c:otherwise>
                                                        <%-- 다른 상태일 때 버튼 예시 (원하면 삭제 가능) --%>
                                                        <button type="button" class="badge bg-success"
                                                                style="width: max-content">예약 가능
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </h6>
                                        </div>

                                        <p class="text-muted mb-2">${lecture.tutorName}</p>
                                        <span class="mb-2"><fmt:formatNumber value="${lecture.price}"
                                                                             type="number"/>원</span>
                                        <div class="card-review">
                                            <div>
                                                <span>⭐ <fmt:formatNumber value="${avgStarMap[lecture.lectureId]}"
                                                                          type="number" maxFractionDigits="1"/></span>
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

 <!-- ✅ 페이지네이션 시작 -->
<c:set var="pageGroupSize" value="5" />
<c:set var="currentPage" value="${lecturePage.number + 1}" /> <!-- 0-based → 1-based -->
<c:set var="totalPages" value="${lecturePage.totalPages}" />

<!-- ✅ ‘고정 그룹 유지’ 계산 로직 -->
<c:set var="currentGroup" value="${((currentPage - 1) / pageGroupSize) - ((currentPage - 1) mod pageGroupSize) / pageGroupSize}" />
<c:set var="startPage" value="${(currentGroup * pageGroupSize) + 1}" />
<c:set var="endPage" value="${startPage + pageGroupSize - 1}" />
<c:if test="${endPage > totalPages}">
    <c:set var="endPage" value="${totalPages}" />
</c:if>

<nav class="pagination-wrap mt-5">
  <ul class="pagination justify-content-center">

    <!-- ◀ 한 페이지 이전 -->
    <c:if test="${not lecturePage.first}">
      <li class="page-item">
        <a class="page-link" href="?page=${lecturePage.number - 1}">
          <i class="bi bi-chevron-left"></i>
        </a>
      </li>
    </c:if>

    <!-- 현재 그룹 페이지 번호 -->
    <c:forEach var="i" begin="${startPage}" end="${endPage}">
      <li class="page-item ${i == currentPage ? 'active' : ''}">
        <a class="page-link" href="?page=${i - 1}">${i}</a>
      </li>
    </c:forEach>

    <!-- ▶ 한 페이지 다음 -->
    <c:if test="${not lecturePage.last}">
      <li class="page-item">
        <a class="page-link" href="?page=${lecturePage.number + 1}">
          <i class="bi bi-chevron-right"></i>
        </a>
      </li>
    </c:if>

  </ul>
</nav>
<!-- ✅ 페이지네이션 끝 -->


    </section>

    <script src="/js/lectureList.js"></script>
</main>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>