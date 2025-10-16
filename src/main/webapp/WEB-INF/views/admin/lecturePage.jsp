<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <!-- 메인 콘텐츠 -->
    <div class="container-fluid py-4 px-5">
        <div class="row">
            <div class="col-12">
                <div class="text-end mb-3">
                    <a href="lectureForm" class="btn btn-success">새 강의 등록</a>
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
                                    <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">번호</th>
                                    <th>제목</th>
                                    <th>내용</th>
                                    <th>관리</th>
                                </tr>
                                </thead>
                                <tbody>
                                <%-- <c:forEach var="notice" items="${noticeList}"> --%>
                                <tr>
                                    <td class="text-center">1</td>
                                    <td class="text-center">추석 맞이 개편</td>
                                    <td class="text-center">추석 맞이 개편을 합니다</td>
                                    <td class="text-center">
                                        <a href="#" class="btn btn-sm btn-primary">수정</a>
                                        <form action="/notice/delete" method="post" style="display:inline;">
                                            <input type="hidden" name="id" value="${notice.id}">
                                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('정말 삭제하시겠습니까?');">삭제</button>
                                        </form>
                                    </td>
                                </tr>
                                <%-- </c:forEach> --%>
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
