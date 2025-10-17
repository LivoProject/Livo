<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <!-- 메인 콘텐츠 -->
    <div class="container-fluid py-4 px-5">
        <div class="row">
            <div class="col-12">
                <div class="text-end mb-3">
                    <a href="lecture/insert" class="btn btn-success">새 강의 등록</a>
                </div>
                <div class="card mb-4">
                    <div class="card-body">
                        <!-- 카테고리 버튼 -->
                        <div class="mb-3">
                            <div class="btn-group" role="group" aria-label="카테고리">
                                <button type="button" class="btn btn-outline-secondary active">전체</button>
                                <button type="button" class="btn btn-outline-secondary">IT</button>
                                <button type="button" class="btn btn-outline-secondary">자기개발</button>
                                <button type="button" class="btn btn-outline-secondary">문화여가</button>
                                <button type="button" class="btn btn-outline-secondary">건강</button>
                                <button type="button" class="btn btn-outline-secondary">언어</button>
                                <button type="button" class="btn btn-outline-secondary">인문사회</button>
                                <button type="button" class="btn btn-outline-secondary">자격증</button>
                                <button type="button" class="btn btn-outline-secondary">경제</button>
                            </div>
                        </div>
                        <div class="row g-2 align-items-center flex-wrap">
                            <div class="col d-flex align-items-center">
                                <input type="text" class="form-control" placeholder="강의명, 강사명 검색...">
                            </div>

                            <div class="col-auto">
                                <button class="btn btn-primary">검색</button>
                            </div>
                        </div>
                        <!-- 필터 영역 -->
                        <div class="row g-3 mt-2 align-items-center">
                            <div class="col-6">
                                <div class="col-auto d-flex align-items-center">
                                    <label class="me-4 col-form-label fw-semibold">예약 상태</label>
                                    <select class="form-select">
                                        <option>전체</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="col-auto d-flex align-items-center">
                                    <label class="me-4 mb-0 fw-semibold">가격</label>
                                    <select class="form-select">
                                        <option>전체</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <!-- 날짜 필터 -->
                        <div class="row g-3 mt-2 align-items-center">
                            <!--강의 기간-->
                            <div class="col-12">
                                <div class="row align-items-center">
                                    <div class="col-auto">
                                        <label class="col-form-label fw-semibold">강의 기간</label>
                                    </div>
                                    <div class="col">
                                        <div class="d-flex align-items-center">
                                            <input type="date" class="form-control me-2">
                                            <span>~</span>
                                            <input type="date" class="form-control ms-2">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- 예약 기간 -->
                        <div class="row g-3 mt-2 align-items-center">
                            <div class="col-12">
                                <div class="row align-items-center">
                                    <div class="col-auto">
                                        <label class="col-form-label fw-semibold">예약 기간</label>
                                    </div>
                                    <div class="col">
                                        <div class="d-flex align-items-center">
                                            <input type="date" class="form-control me-2">
                                            <span>~</span>
                                            <input type="date" class="form-control ms-2">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="card mb-4">
                    <div class="card-header pb-0">
                        <h5>강의 관리</h5>
                    </div>
                    <div class="card-body px-0 pt-0 pb-2">
                        <div class="table-responsive p-0">
                            <table class="table align-items-center mb-0">
                                <thead class="table-light text-center">
                                <tr>
                                    <th>번호</th>
                                    <th>강의명</th>
                                    <th>강사명</th>
                                    <th>예약 기간</th>
                                    <th>강의 기간</th>
                                    <th>신청 인원</th>
                                    <th>수강비</th>
                                    <th>관리</th>
                                </tr>
                                </thead>
                                <tbody>
                                 <c:forEach var="lectures" items="${lectures}" varStatus="status">
                                <tr>
                                    <td class="text-center">${lecturePage.number * lecturePage.size + status.index + 1}</td>
                                    <td class="text-center">${lectures.title}</td>
                                    <td class="text-center">${lectures.tutorName}</td>
                                    <td class="text-center">
                                        <fmt:formatDate value="${lectures.reservationStart}" pattern="yyyy-MM-dd" />
                                        ~
                                        <fmt:formatDate value="${lectures.reservationEnd}" pattern="yyyy-MM-dd" />
                                    </td>
                                    <td class="text-center">
                                        <fmt:formatDate value="${lectures.lectureStart}" pattern="yyyy-MM-dd" />
                                        ~
                                        <fmt:formatDate value="${lectures.lectureEnd}" pattern="yyyy-MM-dd" />
                                    </td>
                                    <td class="text-center">${lectures.reservationCount}/${lectures.totalCount}</td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${lectures.price == 0}">무료</c:when>
                                            <c:otherwise>${lectures.price}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <a href="/admin/lecture/edit?lectureId=${lectures.lectureId}" class="btn btn-sm btn-primary">수정</a>
                                        <form action="/admin/lecture/delete" method="post" style="display:inline;">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <input type="hidden" name="lectureId" value="${lectures.lectureId}">
                                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('정말 삭제하시겠습니까?');">삭제</button>
                                        </form>
                                    </td>
                                </tr>
                                 </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="d-flex justify-content-center mt-4">
                    <nav>
                        <ul class="pagination">
                            <c:forEach begin="0" end="${lecturePage.totalPages - 1}" var="i">
                                <li class="page-item ${i == lecturePage.number ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}&size=9">${i + 1}</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
    </div>
</main>
</div>
</div>

</body>

</html>
