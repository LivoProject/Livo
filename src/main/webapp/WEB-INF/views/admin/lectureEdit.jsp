<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<link rel="stylesheet" href="/css/admin/lecture.css"/>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <!-- 메인 콘텐츠 -->
    <div class="container-fluid py-4 px-5">
        <div class="form-section">
            <h5 class="mb-4 fw-bold">1단계 강의 수정</h5>

            <form id="lectureEditForm" method="post" enctype="multipart/form-data">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                <input type="hidden" name="lectureId" value="${lecture.lectureId}"/>
                <div class="mb-3">
                    <label class="form-label fw-semibold">썸네일</label>
                    <div class="d-flex align-items-center gap-3">
                        <img id="lectureThumbnailPreview"
                             src="${lecture.thumbnailUrl}"
                             alt="thumbnail"
                             class="img-thumbnail">

                        <div class="d-flex flex-column gap-2">
                            <input type="file" id="thumbnailFile" class="form-control" accept="image/*">
                            <div class="d-flex gap-2">
                                <button type="button" id="uploadThumbnailBtn" class="btn btn-outline-primary btn-sm">썸네일 업로드</button>
                                <button type="button" id="resetThumbnailBtn" class="btn btn-outline-secondary btn-sm">기본 썸네일로</button>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- 기본 정보 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">강의 제목</label>
                        <input type="text" name="title" class="form-control" value="${lecture.title}" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강사 이름</label>
                        <input type="text" name=tutorName class="form-control" value="${lecture.tutorName}" required>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label">강사 소개</label>
                    <textarea name="tutorInfo" class="form-control" rows="2">${lecture.tutorInfo}</textarea>
                </div>

                <!-- 카테고리 -->
                <input type="hidden" id="selectedParentId" value="${lecture.category.parent != null ? lecture.category.parent.categoryId : ''}">
                <input type="hidden" id="selectedChildId" value="${lecture.category.categoryId}">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">상위 카테고리</label>
                        <select id="parentCategory" class="form-select">
                            <option value="">상위 카테고리 선택</option>
                            <c:forEach var="p" items="${parents}">
                                <option value="${p.categoryId}"
                                    <c:if test="${lecture.category.parent != null && lecture.category.parent.categoryId == p.categoryId}">
                                        selected
                                    </c:if>>
                                    ${p.categoryName}
                                </option>
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
                        <input type="number" id="totalCount" name="totalCount" class="form-control" min="1"
                        value="${lecture.totalCount}" <c:if test="${lecture.isFree}">disabled</c:if>>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강의비</label>
                        <div class="input-group">
                            <input type="number" id="price" name="price" class="form-control" value="${lecture.price}" <c:if test="${lecture.isFree}">disabled</c:if>>
                            <div class="input-group-text">
                                <input type="checkbox" id="isFree" name="isFree" <c:if test="${lecture.isFree}">checked</c:if>>
                                <label for="isFree" class="ms-1 mb-0">무료강의</label>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 기간 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">예약 시작일</label>
                        <input type="datetime-local" id="reservationStart" name="reservationStart" class="form-control" value="${lecture.reservationStart}" <c:if test="${lecture.isFree}">disabled</c:if>>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">예약 종료일</label>
                        <input type="datetime-local" id="reservationEnd" name="reservationEnd" class="form-control" value="${lecture.reservationEnd}" <c:if test="${lecture.isFree}">disabled</c:if>>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">강의 시작일</label>
                        <input type="date" id="lectureStart" name="lectureStart" class="form-control" value="${lecture.lectureStart}" <c:if test="${lecture.isFree}">disabled</c:if>>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강의 종료일</label>
                        <input type="date" id="lectureEnd" name="lectureEnd" class="form-control" value="${lecture.lectureEnd}" <c:if test="${lecture.isFree}">disabled</c:if>>
                    </div>
                </div>

                <!-- 강의 내용 -->
                <div class="mb-4">
                    <label class="form-label">강의 내용</label>
                    <textarea id="summernote" name="content">${lecture.content}</textarea>
                </div>

                <button type="submit" class="btn btn-primary w-sm-100">저장 후 챕터 수정</button>
            </form>
        </div>
    </div>
</main>
</div>
</div>
<!-- Summernote -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-lite.min.js"></script>
<script src="/js/admin/lecture.js"></script>
<script src="/js/admin/lectureEdit.js"></script>

</body>
</html>
