<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <div class="container-fluid py-4 px-5">
        <div class="row">
            <div class="col">
                <div class="card mb-4">
                    <div class="card-header">
                        <ul class="nav nav-pills card-header-pills" id="statsTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="tab1-tab" data-bs-toggle="pill" data-bs-target="#tab1" type="button" role="tab">인기 강좌 Top5</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="tab2-tab" data-bs-toggle="pill" data-bs-target="#tab2" type="button" role="tab">월별 매출</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="tab3-tab" data-bs-toggle="pill" data-bs-target="#tab3" type="button" role="tab">강사별 운영 현황</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="tab4-tab" data-bs-toggle="pill" data-bs-target="#tab4" type="button" role="tab">월별 가입자 수</button>
                            </li>
                        </ul>
                    </div>
                    <div class="card-body">
                        <div class="tab-content">
                            <div class="tab-pane fade show active" id="tab1" role="tabpanel">
                                <canvas id="chart1"></canvas>
                            </div>
                            <div class="tab-pane fade" id="tab2" role="tabpanel">
                                <canvas id="chart2"></canvas>
                            </div>
                            <div class="tab-pane fade" id="tab3" role="tabpanel">
                                <canvas id="chart3"></canvas>
                            </div>
                            <div class="tab-pane fade" id="tab4" role="tabpanel">
                                <canvas id="chart4"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-6 mb-4">
                <div class="card">
                    <div class="card-body px-4">
                        <div class="row">
                            <div class="col">
                                <div class="card-title fs-4 fw-semibold">강의 관리</div>
                                <div class="card-subtitle text-body-secondary mb-4"></div>
                            </div>
                        </div>
                        <div class="table-responsive small">
                            <table class="table table-striped table-sm">
                                <thead>
                                <tr>
                                    <th class="text-center">#</th>
                                    <th class="text-center">강의명</th>
                                    <th class="text-center">강사명</th>
                                    <th class="text-center">관리</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="lecture" items="${recentLectures}" varStatus="status">
                                <tr>
                                    <td class="text-center">${status.index + 1}</td>
                                    <td class="text-center">${lecture.title}</td>
                                    <td class="text-center">${lecture.tutorName}</td>
                                    <td class="text-center">
                                        <button class="btn btn-sm editBtn"
                                                data-type="lecture"
                                                data-id="${lecture.lectureId}"
                                                title="수정">
                                            <i class="bi bi-pencil-square text-secondary"></i>
                                        </button>
                                        <button class="btn btn-sm deleteBtn"
                                                data-type="lecture"
                                                data-id="${lecture.lectureId}"
                                                title="삭제">
                                            <i class="bi bi-trash text-secondary"></i>
                                        </button>
                                    </td>
                                </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-6 mb-4">
                <div class="card">
                    <div class="card-body px-4">
                        <div class="row">
                            <div class="col">
                                <div class="card-title fs-4 fw-semibold">신고 관리</div>
                                <div class="card-subtitle text-body-secondary mb-4"></div>
                            </div>
                        </div>

                        <div class="table-responsive small">
                            <table class="table table-striped table-sm">
                                <thead>
                                <tr>
                                    <th class="text-center">#</th>
                                    <th class="text-center">리뷰내용</th>
                                    <th class="text-center">신고사유</th>
                                    <th class="text-center">관리</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="report" items="${reports}" varStatus="status">
                                <tr>
                                    <td class="text-center">${status.index + 1}</td>
                                    <td class="text-center">
                                    <c:choose>
                                        <c:when test="${fn:length(report.review.reviewContent) > 100}">
                                          ${fn:substring(report.review.reviewContent, 0, 100)}...
                                        </c:when>
                                        <c:otherwise>
                                          ${report.review.reviewContent}
                                        </c:otherwise>
                                    </c:choose>
                                    </td>
                                    <td class="text-center">${report.reportReason}</td>
                                    <td class="text-center">
                                        <button class="btn btn-sm update-status"
                                                data-action="approve"
                                                data-id="${report.reportId}"
                                                title="승인">
                                            <i class="bi bi-check fs-4 text-success"></i>
                                        </button>
                                        <button class="btn btn-sm update-status"
                                                data-action="reject"
                                                data-id="${report.reportId}"
                                                title="반려">
                                            <i class="bi bi-x fs-4 text-danger"></i>
                                        </button>
                                    </td>
                                </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-lg-6 mb-4">
                <div class="card">
                    <div class="card-body px-4">
                        <div class="row">
                            <div class="col">
                                <div class="card-title fs-4 fw-semibold">공지사항 관리</div>
                                <div class="card-subtitle text-body-secondary mb-4"></div>
                            </div>
                        </div>
                        <div class="table-responsive small">
                            <table class="table table-striped table-sm">
                                <thead>
                                <tr>
                                    <th class="text-center">#</th>
                                    <th class="text-center">제목</th>
                                    <th class="text-center">내용</th>
                                    <th class="text-center">관리</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="notice" items="${notices}" varStatus="status">
                                <tr>
                                    <td class="text-center">${status.index + 1}</td>
                                    <td class="text-center">${notice.title}</td>
                                    <td class="text-center">
                                    <c:choose>
                                        <c:when test="${fn:length(notice.content) > 50}">
                                          ${fn:substring(notice.content, 0, 50)}...
                                        </c:when>
                                        <c:otherwise>
                                          ${notice.content}
                                        </c:otherwise>
                                    </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <button class="btn btn-sm toggle-notice-status"
                                                data-action="visible"
                                                data-id="${notice.id}"
                                                title="${notice.visible ? '숨기기' : '노출하기'}">
                                            <i class="bi ${notice.visible ? 'bi-eye text-success' : 'bi-eye-slash text-secondary'}"></i>
                                        </button>
                                        <button class="btn btn-sm toggle-notice-status"
                                                data-action="pin"
                                                data-id="${notice.id}"
                                                title="${notice.pinned ? '고정 해제' : '상단 고정'}">
                                            <i class="bi ${notice.pinned ? 'bi-pin-angle-fill text-warning' : 'bi-pin text-secondary'}"></i>
                                        </button>
                                    </td>
                                </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-6 mb-4">
                <div class="card">
                    <div class="card-body px-4">
                        <div class="row">
                            <div class="col">
                                <div class="card-title fs-4 fw-semibold">FAQ 관리</div>
                                <div class="card-subtitle text-body-secondary mb-4"></div>
                            </div>
                        </div>
                        <div class="table-responsive small">
                            <table class="table table-striped table-sm">
                                <thead>
                                <tr>
                                    <th class="text-center">#</th>
                                    <th class="text-center">질문</th>
                                    <th class="text-center">관리</th>
                                </tr>
                                </thead>
                                <tbody>
                                 <c:forEach var="faq" items="${recentFaqs}" varStatus="status">
                                <tr>
                                    <td class="text-center">${status.index + 1}</td>
                                    <td class="text-center">${faq.question}</td>
                                    <td class="text-center">
                                        <button class="btn btn-sm editBtn"
                                                data-type="faq"
                                                data-id="${faq.id}"
                                                title="수정">
                                            <i class="bi bi-pencil-square text-secondary"></i>
                                        </button>
                                        <button class="btn btn-sm deleteBtn"
                                                data-type="faq"
                                                data-id="${faq.id}"
                                                title="삭제">
                                            <i class="bi bi-trash text-secondary"></i>
                                        </button>
                                    </td>
                                </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </div>
</main>
</div>
<script src="/js/admin/dashboard.js"></script>
<script src="/js/admin/dashboard-chart.js"></script>
</body>

</html>
