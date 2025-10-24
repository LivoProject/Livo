<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>

    <div class="container-fluid py-4">
        <!-- 필터 -->
        <div class="lv-card lv-filter">
            <div class="row g-3 align-items-end">
                <div class="col-md-3">
                    <label class="form-label">From</label>
                    <input type="date" id="from" class="form-control">
                </div>
                <div class="col-md-3">
                    <label class="form-label">To</label>
                    <input type="date" id="to" class="form-control">
                </div>
                <div class="col-md-3">
                    <label class="form-label d-block"> </label>
                    <button id="load" class="btn btn-primary w-100">불러오기</button>
                </div>
            </div>
        </div>

        <!-- 콘텐츠 -->
        <div class="row g-4">
            <div class="col-lg-8">
                <div class="lv-card">
                    <div class="lv-card-header">
                        <h5 class="mb-0">인기 강좌 Top5</h5>
                    </div>
                    <div class="lv-chart-wrap">
                        <canvas id="topLectures"></canvas>
                        <div class="lv-empty" id="chart-empty" hidden>데이터가 없습니다.</div>
                        <div class="lv-loading" id="chart-loading" hidden>불러오는 중…</div>
                        <div class="lv-error" id="chart-error" hidden>오류가 발생했습니다.</div>
                    </div>
                </div>
            </div>

            <div class="col-lg-4">
                <div class="lv-card">
                    <div class="lv-card-header">
                        <h6 class="mb-0">상세(Top5)</h6>
                    </div>
                    <div class="lv-table-wrap">
                        <table id="topTable" class="table table-sm table-hover align-middle">
                            <thead>
                            <tr>
                                <th>강좌</th>
                                <th class="text-end">전체</th>
                                <th class="text-end text-success">확정</th>
                                <th class="text-end text-danger">취소</th>
                                <th class="text-end text-warning">대기</th>
                                <th class="text-end">예약률</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div class="lv-empty" id="table-empty" hidden>표시할 데이터가 없습니다.</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- 페이지 전용 JS -->
    <link rel="stylesheet" href="<c:url value='/css/admin/chart.css'/>">
    <script defer src="<c:url value='/js/admin/chart.js'/>"></script>
</main>
