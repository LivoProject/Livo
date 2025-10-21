<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 강의 -->
    <main class="main-content">
        <h3>즐겨찾는 강의</h3>
        <div class="lecture-grid large">
            <!-- 카드 1 -->
            <c:forEach var="lecture" items="${likedLectures}">
                <div class="card">
                    <img src="${lecture.thumbnailUrl}" class="card-img-top" alt="${lecture.title}">

                    <div class="card-body">
                        <h5 class="card-title">${lecture.title}</h5>
                        <p>${lecture.tutorName}∣<fmt:formatNumber value="${lecture.price}" type="number"/> 원</p>
                        <div class="progress mt-2">
                            <div class="progress-bar" style="width: 60%"></div>
                        </div>
                    </div>
                    <div class="card-footer bg-white d-flex justify-content-between align-items-center">
                        <div>
                            <button class="btn-unlike btn-main" data-lecture-id="${lecture.lectureId}">해제</button>
                        </div>
                        <small class="text-muted">9 mins</small>
                    </div>
                </div>
            </c:forEach>


        </div>
    </main>

</section>


<!-- 컨텐츠 끝 -->

<%@ include file="/WEB-INF/views/common/footer.jsp" %>