<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage-modal.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 강의 -->
    <main class="main-content">
        <h3>즐겨찾는 강의</h3>
        <select class="form-select" aria-label="Default select example">
            <option selected>최신순</option>
            <option value="2">오래된순</option>
            <option value="3">인기순</option>
        </select>
        <div class="lecture-grid large">
            <!-- 카드 1 -->
            <c:if test="${not empty likedLectures}">
                <c:forEach var="lecture" items="${likedLectures}">
                    <div class="card">
                        <div class="card-img-wrap">
                            <a href="/lecture/content/${lecture.lectureId}">
                                <img src="${lecture.thumbnailUrl}" class="card-img-top" alt="${lecture.title}"/>
                                <button class="play-btn">
                                    <i class="bi bi-play-fill"></i>
                                </button>
                            </a>
                        </div>

                        <div class="card-body">
                            <a href="/lecture/content/${lecture.lectureId}">
                                <h6 class="fw-bold text-ellipsis-2 lecture-title">${lecture.title}</h6>
                                <p class="text-muted mb-3">${lecture.tutorName}</p>
                                <span><fmt:formatNumber value="${lecture.price}" type="number"/>원</span>
                                <div class="progress" style="height: 8px;">
                                    <div class="progress-bar bg-success"
                                         style="width: ${lecture.progressPercent}%;"></div>
                                </div>
                                <small class="text-muted">${lecture.progressPercent}%</small>
                            </a>
                        </div>

                        <div class="card-footer">
                            <div class="button-wrap">
                                <c:choose>
                                    <c:when test="${lecture.reserved == true}">
                                        <button class="btn-unlike btn-main"
                                                data-lecture-id="${lecture.lectureId}"
                                                data-bs-toggle="modal"
                                                data-bs-target="#likeModal">
                                            삭제하기
                                        </button>
                                       <a href="/lecture/content/${lecture.lectureId}#review" class="btn-cancel">수강평
                                            작성</a>
                                    </c:when>

                                    <c:otherwise>
                                        <button class="btn-unlike btn-main"
                                                data-lecture-id="${lecture.lectureId}"
                                                data-bs-toggle="modal"
                                                data-bs-target="#likeModal">
                                            삭제하기
                                        </button>
                                    </c:otherwise>
                                </c:choose>

                            </div>
                        </div>
                    </div>

                </c:forEach>
            </c:if>
            <c:if test="${empty likedLectures}">
                <p class="text-muted">좋아요한 강의가 없습니다.</p>
            </c:if>
        </div>

        <%@ include file="/WEB-INF/views/common/pagination.jsp" %>

    </main>

</section>
<!-- 컨텐츠 끝 -->

<!-- 모달 -->
<%@ include file="/WEB-INF/views/common/modal.jsp" %>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>