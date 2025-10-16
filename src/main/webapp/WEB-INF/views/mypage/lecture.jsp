<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- 컨텐츠-->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 강의 -->
    <main class="main-content">
        <h3>내 강의실</h3>
        <div class="lecture-grid">
            <!-- 카드 1 -->
            <div class="card">
                <img
                    src="https://picsum.photos/400/200?random=1"
                    class="card-img-top"
                    alt="강의 썸네일"
                />
                <div class="card-body">
                    <h6 class="fw-bold mb-1">스프링 부트 입문</h6>
                    <p class="text-muted small mb-2">홍길동 강사</p>
                    <p class="text-truncate">
                    스프링 부트를 처음 배우는 입문자용 강의입니다.
                    </p>
                    <div class="progress mt-2">
                    <div class="progress-bar" style="width: 60%"></div>
                    </div>
                </div>
                <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                    <div>
                        <a href="mypage-lecture-view.html" class="btn btn-sm">이어보기</a>
                        <button class="btn btn-sm btn-primary"
                                data-bs-toggle="modal"
                                data-bs-target="#exampleModal">
                          수강평 작성
                        </button>
                     </div>
                    <small class="text-muted">9 mins</small>
                </div>
            </div>

            <!-- 카드 2 -->
            <div class="card">
                <img
                    src="https://picsum.photos/400/200?random=2"
                    class="card-img-top"
                    alt="강의 썸네일"
                />
                <div class="card-body">
                    <h6 class="fw-bold mb-1">JavaScript 심화</h6>
                    <p class="text-muted small mb-2">이민영 강사</p>
                    <p class="text-truncate">
                    비동기 처리, ES6 문법 등 자바스크립트의 핵심을 다룹니다.
                    </p>
                    <div class="progress mt-2">
                    <div class="progress-bar" style="width: 30%"></div>
                    </div>
                </div>
                <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                    <div>
                        <a href="mypage-lecture-view.html" class="btn btn-sm">이어보기</a>
                       <button class="btn btn-sm btn-primary"
                               data-bs-toggle="modal"
                               data-bs-target="#exampleModal">
                         수강평 작성
                       </button>
                    </div>
                    <small class="text-muted">15 mins</small>
                </div>
            </div>

            <!-- 카드 3 -->
            <div class="card">
                <img
                    src="https://picsum.photos/400/200?random=3"
                    class="card-img-top"
                    alt="강의 썸네일"
                />
                <div class="card-body">
                    <h6 class="fw-bold mb-1">데이터베이스 기초</h6>
                    <p class="text-muted small mb-2">서준서 강사</p>
                    <p class="text-truncate">
                    SQL 문법과 ERD 설계 개념을 중심으로 학습합니다.
                    </p>
                    <div class="progress mt-2">
                    <div class="progress-bar" style="width: 80%"></div>
                    </div>
                </div>
                <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                    <div>
                        <a href="mypage-lecture-view.html" class="btn btn-sm">이어보기</a>
                        <button class="btn btn-sm btn-primary"
                                data-bs-toggle="modal"
                                data-bs-target="#exampleModal">
                          수강평 작성
                        </button>
                    </div>
                    <small class="text-muted">27 mins</small>
                </div>
            </div>
        </div>
    </main>

    <!-- Modal -->
    <%@ include file="/WEB-INF/views/common/modal.jsp" %>


</section>
<!-- 컨텐츠 끝 -->

<%@ include file="/WEB-INF/views/common/footer.jsp" %>