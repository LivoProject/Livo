<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>

    <div class="container-fluid py-4">
        <!-- =========================
             필터
        ========================== -->
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
                    <label class="form-label d-block">&nbsp;</label>
                    <button id="load" class="btn btn-primary w-100">불러오기</button>
                </div>
            </div>
        </div>

        <!-- =========================
             회원 성장 추이 (월별 가입자 수)
        ========================== -->
        <div class="row g-4 mt-1">
            <div class="col-12">
                <div class="lv-card">
                    <div class="lv-card-header d-flex align-items-center justify-content-between">
                        <h5 class="mb-0">회원 성장 추이 (월별 가입자 수)</h5>
                    </div>
                    <div class="lv-chart-wrap">
                        <canvas id="signupChart" height="110"></canvas>
                        <div class="lv-empty"  id="signup-empty"  hidden>데이터가 없습니다.</div>
                        <div class="lv-loading" id="signup-loading" hidden>불러오는 중…</div>
                        <div class="lv-error"  id="signup-error"  hidden>오류가 발생했습니다.</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- =========================
             매출/결제 현황 (월별)
        ========================== -->
        <div class="row g-4 mt-1">
            <div class="col-12">
                <div class="lv-card">
                    <div class="lv-card-header d-flex align-items-center justify-content-between">
                        <h5 class="mb-0">매출/결제 현황 (월별)</h5>
                    </div>
                    <div class="lv-chart-wrap">
                        <canvas id="revenueChart" height="110"></canvas>
                        <div class="lv-empty"  id="revenue-empty"  hidden>데이터가 없습니다.</div>
                        <div class="lv-loading" id="revenue-loading" hidden>불러오는 중…</div>
                        <div class="lv-error"  id="revenue-error"  hidden>오류가 발생했습니다.</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- =========================
             강사별 강의 운영 현황 (Top5)
             - 좌: 차트 / 우: 상세표
        ========================== -->
        <div class="row g-3 mt-1 mb-4">
            <!-- 좌측: 강사 차트 -->
            <div class="col-lg-8">
                <div class="lv-card">
                    <div class="lv-card-header">
                        <h6 class="mb-0">강사별 강의 운영 현황 (Top5)</h6>
                    </div>
                    <div class="lv-chart-wrap">
                        <canvas id="instructorOpsChart" height="140"></canvas>
                        <div id="instructor-empty" class="lv-empty" hidden>데이터가 없습니다.</div>
                        <div id="instructor-loading" class="lv-loading" hidden>불러오는 중...</div>
                        <div id="instructor-error" class="lv-error" hidden>오류가 발생했습니다.</div>
                    </div>
                </div>
            </div>

            <!-- 우측: 강사 상세표 -->
            <div class="col-lg-4">
                <div class="lv-card">
                    <div class="lv-card-header">
                        <h6 class="mb-0">강사 Top5 (상세)</h6>
                    </div>
                    <div class="lv-table-wrap">
                        <table id="instructorTable" class="table table-sm align-middle">
                            <thead>
                            <tr>
                                <th>강사</th>
                                <th class="text-end text-success">확정</th>
                                <th class="text-end text-warning">대기</th>
                                <th class="text-end text-danger">취소</th>
                                <th class="text-end">매출</th>
                                <th class="text-end">확정률</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                        <div id="instructor-table-empty" class="lv-empty" hidden>표시할 데이터가 없습니다.</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- =========================
             인기 강좌 Top5 (차트 + 표)
        ========================== -->
        <div class="row g-4">
            <!-- 좌: 차트 -->
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

            <!-- 우: 상세 테이블 -->
            <div class="col-lg-4">
                <div class="lv-card">
                    <div class="lv-card-header">
                        <h6 class="mb-0">인기 강좌(Top5)</h6>
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

    <!-- 페이지 전용 스타일/스크립트 -->
    <link rel="stylesheet" href="<c:url value='/css/admin/chart.css'/>">
    <script defer src="<c:url value='/js/admin/chart.js'/>"></script>

    <!-- 화면 밸런스용(선택) -->
    <style>
        /* 차트 최대 높이 살짝 제한해 균형 맞춤 */
        .lv-chart-wrap canvas { max-height: 360px; }
        /* 테이블 셀 한 줄 표시 + 말줄임 */
        #instructorTable td, #topTable td {
            white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 220px;
        }
    </style>
</main>
