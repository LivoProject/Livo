<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>
<link rel="stylesheet" href="/css/admin/chart.css"/>
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

        <!-- ============ 탭 헤더 ============ -->
        <div class="lv-card lv-filter mt-3">
            <div class="lv-tabs">
                <button class="lv-tab is-active" data-tab="members">월별 가입자 수</button>
                <button class="lv-tab" data-tab="revenue">매출/결제(월별)</button>
                <button class="lv-tab" data-tab="instructors">강사 Top5</button>
                <button class="lv-tab" data-tab="lectures">인기 강좌 Top5</button>

                <div class="lv-tabs-right">
                    <button id="applyFilter" class="btn btn-primary">불러오기</button>
                </div>
            </div>
        </div>


        <!-- ============ 공용 차트 패널 ============ -->
        <div class="lv-card mt-3">
            <div class="lv-card-header d-flex align-items-center justify-content-between">
                <h5 id="panel-title" class="mb-0">회원 성장 추이 (월별 가입자 수)</h5>
            </div>

            <div class="lv-chart-wrap">
                <canvas id="mainChart" height="140"></canvas>
                <div class="lv-empty"  id="panel-empty"  hidden>데이터가 없습니다.</div>
                <div class="lv-loading" id="panel-loading" hidden>불러오는 중…</div>
                <div class="lv-error"  id="panel-error"  hidden>오류가 발생했습니다.</div>
            </div>
        </div>

        <!-- ============ (랭킹 탭 전용) 우측 표 ============ -->
        <div id="side-table-card" class="lv-card mt-3" hidden>
            <div class="lv-card-header">
                <h6 id="side-title" class="mb-0">상세</h6>
            </div>
            <div class="lv-table-wrap">
                <table id="sideTable" class="table table-sm align-middle">
                    <thead id="sideThead"></thead>
                    <tbody id="sideTbody"></tbody>
                </table>
                <div id="side-empty" class="lv-empty" hidden>표시할 데이터가 없습니다.</div>
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
