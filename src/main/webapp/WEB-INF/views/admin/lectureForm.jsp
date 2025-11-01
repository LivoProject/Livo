<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<link rel="stylesheet" href="<c:url value='/css/admin/lecture.css'/>"/>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <!-- 메인 콘텐츠 -->
    <div class="container-fluid py-4 px-5">
        <div class="form-section">
            <h5 class="mb-4 fw-bold">1단계: 강의 등록</h5>
            <form id="lectureForm" action="/admin/lecture/save" method="post" enctype="multipart/form-data">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <!-- 기본 정보 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">강의 제목</label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강사 이름</label>
                        <input type="text" name=tutorName class="form-control" required>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label">강사 소개</label>
                    <textarea name="tutorInfo" class="form-control" rows="2"></textarea>
                </div>

                <!-- 카테고리 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">상위 카테고리</label>
                        <select id="parentCategory" class="form-select">
                            <option value="">상위 카테고리 선택</option>
                            <c:forEach var="p" items="${parents}">
                                <option value="${p.categoryId}">${p.categoryName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">하위 카테고리</label>
                        <select id="childCategory" name="categoryId" class="form-select">
                            <option value="">하위 카테고리 선택</option>
                        </select>
                    </div>
                </div>

                <!-- 강의 인원, 비용 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">모집 인원</label>
                        <input type="number" id="totalCount" name="totalCount" class="form-control" min="1" value="10">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강의비</label>
                        <div class="input-group">
                            <input type="number" id="price" name="price" class="form-control" value="0">
                            <div class="input-group-text">
                                <input type="checkbox" id="isFree" name="isFree" value="true">
                                <label for="isFree" class="ms-1 mb-0">무료강의</label>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 기간 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">예약 시작일</label>
                        <input type="datetime-local" id="reservationStart" name="reservationStart" class="form-control">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">예약 종료일</label>
                        <input type="datetime-local" id="reservationEnd" name="reservationEnd" class="form-control">
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">강의 시작일</label>
                        <input type="date" id="lectureStart" name="lectureStart" class="form-control">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강의 종료일</label>
                        <input type="date" id="lectureEnd" name="lectureEnd" class="form-control">
                    </div>
                </div>
                <!-- 강의 내용 -->
                <div class="mb-4">
                    <label class="form-label">강의 내용</label>
                    <textarea id="summernote" name="content"></textarea>
                </div>

                <button id="nextStepBtn" class="btn btn-primary w-sm-100">다음 단계</button>
            </form>
        </div>
    </div>
</main>
</div>
</div>
<script src="<c:url value='/js/admin/lecture.js'/>"></script>
<script src="<c:url value='/js/admin/lectureForm.js'/>"></script>
</body>
</html>