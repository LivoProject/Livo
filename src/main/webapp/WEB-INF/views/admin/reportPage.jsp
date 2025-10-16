<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

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
                            <table class="table align-items-center mb-0">
                                <thead class="table-light text-center">
                                <tr>
                                    <th>번호</th>
                                    <th>제목</th>
                                    <th>내용</th>
                                    <th>신고자</th>
                                    <th>신고일자</th>
                                    <th>관리</th>
                                </tr>
                                </thead>
                                <tbody>
                                <!-- <c:forEach var="notice" items="${noticeList}"> -->
                                <tr>
                                    <td class="text-center">1</td>
                                    <td class="text-center">신고제목</td>
                                    <td class="text-center">신고내용</td>
                                    <td class="text-center">오연희</td>
                                    <td class="text-center">2025-10-15</td>
                                    <td class="text-center">
                                        <a href="#" class="btn btn-sm btn-primary">수정</a>
                                        <form action="/notice/delete" method="post" style="display:inline;">
                                            <input type="hidden" name="id" value="${notice.id}">
                                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('정말 삭제하시겠습니까?');">삭제</button>
                                        </form>
                                    </td>
                                </tr>
                                <!-- </c:forEach> -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>
</div>
</div>
</body>
</html>

