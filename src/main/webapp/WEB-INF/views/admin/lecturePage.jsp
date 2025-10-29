<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<link rel="stylesheet" href="css/admin/lecture.css"/>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <!-- 메인 콘텐츠 -->
    <div class="container-fluid py-4 px-5">
        <div class="row">
            <div class="col-12">
                <div class="text-end mb-3">
                    <a href="lecture/insert" id="lecInsert" class="btn btn-success">새 강의 등록</a>
                </div>
                <div class="card mb-4">
                    <div class="card-body">
                        <!-- 카테고리 버튼 -->
                        <div class="mb-3">
                            <div id="categoryGroup" class="btn-group" role="group" aria-label="카테고리">
                                <button type="button" class="btn btn-outline-secondary active" data-category-id="">전체</button>
                                <button type="button" class="btn btn-outline-secondary" data-category-id="1">IT</button>
                                <button type="button" class="btn btn-outline-secondary" data-category-id="2">자기개발</button>
                                <button type="button" class="btn btn-outline-secondary" data-category-id="3">문화여가</button>
                                <button type="button" class="btn btn-outline-secondary" data-category-id="4">건강</button>
                                <button type="button" class="btn btn-outline-secondary" data-category-id="5">언어</button>
                                <button type="button" class="btn btn-outline-secondary" data-category-id="6">인문사회</button>
                                <button type="button" class="btn btn-outline-secondary" data-category-id="7">자격증</button>
                                <button type="button" class="btn btn-outline-secondary" data-category-id="8">경제</button>
                            </div>
                        </div>
                        <div class="row g-2 align-items-center flex-wrap">
                            <div class="col d-flex align-items-center">
                                <input id="keyword" type="text" class="form-control" placeholder="강의명, 강사명 검색...">
                            </div>

                            <div class="col-auto">
                                <button id="searchBtn" class="btn btn-primary">검색</button>
                            </div>
                        </div>
                        <!-- 필터 영역 -->
                        <div class="row g-3 mt-2 align-items-center">
                            <div class="col-6">
                                <div class="col-auto d-flex align-items-center">
                                    <label class="me-4 col-form-label fw-semibold">예약 상태</label>
                                    <select id="statusSelect" class="form-select">
                                        <option value="">전체</option>
                                        <option value="OPEN">예약 중</option>
                                        <option value="CLOSED">예약 마감</option>
                                        <option value="ENDED">종료</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-6">
                                <div class="col-auto d-flex align-items-center">
                                    <label class="me-4 mb-0 fw-semibold">가격</label>
                                    <select id="priceSelect" class="form-select">
                                        <option>전체</option>
                                        <option value="paid">유료</option>
                                        <option value="free">무료</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <!-- 날짜 필터 -->
                        <!--강의 기간-->
                        <div class="row g-3 mt-2 align-items-center">
                            <div class="col-12">
                                <div class="row align-items-center">
                                    <div class="col-auto">
                                        <label class="col-form-label fw-semibold">예약 기간</label>
                                    </div>
                                    <div class="col">
                                        <div class="d-flex align-items-center">
                                            <input id="reservationStart" type="date" class="form-control me-2">
                                            <span>~</span>
                                            <input id="reservationEnd" type="date" class="form-control ms-2">
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
                                        <label class="col-form-label fw-semibold">강의 기간</label>
                                    </div>
                                    <div class="col">
                                        <div class="d-flex align-items-center">
                                            <input id="lectureStart" type="date" class="form-control me-2">
                                            <span>~</span>
                                            <input id="lectureEnd" type="date" class="form-control ms-2">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="card mb-4 vertical-scroll-wrap">
                    <div class="card-header pb-0">
                        <h5>강의 관리</h5>
                    </div>
                    <div class="card-body px-0 pt-0 pb-2 vertical-scroll">
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
        </div>
    </div>
</main>
</div>
</div>
<script src="/js/admin/lectureSearch.js"></script>
</body>
</html>
