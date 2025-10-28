<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<link rel="stylesheet" href="/css/admin/report.css"/>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <!-- 메인 콘텐츠 -->
    <div class="container-fluid py-4 px-5">
        <div class="row">
            <div class="col-12">
                <div class="card mb-4">
                    <div class="card-header pb-0">
                        <h5>신고 리스트</h5>
                    </div>
                    <div class="card-body px-0 pt-0 pb-2">
                        <div class="table-responsive p-0">
                            <table id="reportTable" class="table align-middle align-items-center mb-0">
                                <thead class="table-light text-center">
                                <tr>
                                    <th>번호</th>
                                    <th>신고자</th>
                                    <th>사유</th>
                                    <th>신고일자</th>
                                    <th>상태</th>
                                    <th>관리</th>
                                </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
                            <ul id="pagination" class="pagination justify-content-center"></ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>
</div>
</div>
<script src="/js/admin/report.js"></script>
</body>
</html>

