<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>

<!-- 컨텐츠 -->
<section id="mypage" class="container">
    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 메인 컨텐츠 -->
    <main class="main-content">
        <!-- 프로필 / 환영 -->
        <div class="welcome-box mb-4">
            <div class="welcome-text">
                <h3>${mypage.nickname} 님, 반가워요!</h3>
                <p>🌱 <span>${mypage.joinDays}</span>일 째 성장 중</p>
            </div>
            <p>
                진행 중인 강의 <span>${inProgressLectureCount}</span>개 · 이번 주 학습시간 <span>${weeklyStudyHours}</span>시간
            </p>
        </div>

        <!-- 학습 현황 -->
        <div class="card-box-wrap mb-4">
            <div class="card-box recent-card">
                <div class="card-header">
                    <h6>최근 학습 강의</h6>
                    <a href="/mypage/lecture" class="more-link">
                        학습 목록 <i class="bi bi-chevron-right"></i>
                    </a>
                </div>
                <div class="recent-lecture">
                    <a href="/lecture/view/${recentLecture.lecture.lectureId}" class="play-icon">
                        <i class="bi bi-play-fill"></i>
                    </a>

                    <c:if test="${recentLecture != null}">
                        <div class="text-wrap">
                            <a href="/lecture/content/${recentLecture.lecture.lectureId}">${recentLecture.lecture.title}</a>
                            <span>진도율: ${recentLecture.progressPercent}%</span>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- 학습 통계 -->
            <div class="stat-grid">
                <div class="stat-card">
                    <i class="bi bi-clock-history text-primary"></i>
                    <div><b>${totalStudyHours}</b><span>누적 학습시간</span></div>
                </div>
                <div class="stat-card">
                    <i class="bi bi-award text-success"></i>
                    <div><b>${completedLectures}</b><span>완강 강좌</span></div>
                </div>
                <div class="stat-card">
                    <i class="bi bi-calendar-check text-warning"></i>
                    <div><b>${studyDays}</b><span>이번 달 학습일수</span></div>
                </div>
            </div>
        </div>

        <!-- 진행중인 강의 -->
        <div class="card-box mb-4">
            <div class="card-header">
                <h6>진행중인 강의</h6>
                <a href="/mypage/lecture" class="more-link">더보기 <i class="bi bi-chevron-right"></i></a>
            </div>

            <div class="lecture-grid ${fn:length(recentConfirmedLectures) >= 3 ? 'dis-none' : ''}">
                <c:if test="${not empty recentConfirmedLectures}">
                    <c:forEach var="lecture" items="${recentConfirmedLectures}">
                        <div class="card">
                            <div class="card-img-wrap">
                                <a href="/lecture/view/${lecture.lectureId}">
                                    <img src="${lecture.thumbnailUrl}" class="card-img-top" alt="${lecture.title}">
                                    <button class="play-btn"><i class="bi bi-play-fill"></i></button>
                                </a>
                            </div>
                            <div class="card-body">
                                <a href="/lecture/content/${lecture.lectureId}">
                                    <h6 class="card-title lecture-title">${lecture.title}</h6>
                                    <div class="progress" style="height: 8px;">
                                        <div class="progress-bar bg-success"
                                             style="width: ${lecture.progressPercent}%;"></div>
                                    </div>
                                    <small class="text-muted">${lecture.progressPercent}%</small>
                                </a>
                            </div>
                            <div class="card-footer">

                                <div class="button-wrap">
                                    <button class="btn-unreserve btn-main"
                                            data-lecture-id="${lecture.lectureId}"
                                            data-bs-toggle="modal"
                                            data-bs-target="#reserveModal">
                                        예약 취소
                                    </button>
                                    <a href="/lecture/content/${lecture.lectureId}#review" class="btn-cancel">
                                        수강평 작성
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${empty recentConfirmedLectures}">
                    <p class="text-muted">진행 중인 강의가 없습니다.</p>
                </c:if>
            </div>
        </div>


        <!-- 즐겨찾기 -->
        <div class="card-box mb-4">
            <div class="card-header">
                <h6>즐겨찾는 강의</h6>
                <a href="mypage/like" class="more-link">더보기 <i class="bi bi-chevron-right"></i></a>
            </div>
            <div class="lecture-grid ${fn:length(top2LikedLectures) >= 3 ? 'dis-none' : ''}">
                <c:if test="${not empty top2LikedLectures}">
                    <c:forEach var="lecture" items="${top2LikedLectures}">
                        <div class="card">

                            <div class="card-img-wrap">
                                <a href="/lecture/view/${lecture.lectureId}">
                                    <img src="${lecture.thumbnailUrl}" class="card-img-top" alt="${lecture.title}">
                                    <button class="play-btn">
                                        <i class="bi bi-play-fill"></i>
                                    </button>

                                </a>

                            </div>
                            <div class="card-body">
                                <a href="/lecture/content/${lecture.lectureId}">
                                    <h6 class="card-title fw-bold text-ellipsis-2 lecture-title">${lecture.title}</h6>
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
                <c:if test="${empty top2LikedLectures}">
                    <p class="text-muted">좋아요 한 강의가 없습니다.</p>
                </c:if>
            </div>
        </div>


        <!-- 결제/공지 -->
        <div class="card-box">


            <div class="pay-noti-box">
                <!-- 결제내역 -->
                <div class="payment-wrap">
                    <div class="card-header">
                        <h6>결제 내역</h6>
                        <a href="/mypage/payment" class="more-link">
                            더보기
                            <i class="bi bi-chevron-right"></i>
                        </a>
                    </div>
                    <div class="payment-list">
                        <c:forEach var="payment" items="${payments}">
                            <div class="payment-box">
                                <a href="/mypage/payment">
                                    <div class="payment-header">
                                        <div class="title-wrap">
                                            <span class="badge status-${payment.status}">${payment.status}</span>
                                            <span class="order-info text-ellipsis">${payment.orderName}</span>
                                        </div>
                                        <span class="amount">
                                    ₩<fmt:formatNumber value="${payment.amount}" pattern="#,###"/>
                                  </span>
                                    </div>
                                </a>
                            </div>
                        </c:forEach>
                        <c:if test="${empty payments}">
                            <p class="text-muted">결제 내역이 없습니다.</p>
                        </c:if>
                    </div>
                </div>

                <!-- 알림 / 공지 -->
                <div class="notice-wrap">
                    <div class="card-header">
                        <h6>공지사항</h6>
                        <a href="notice/list" class="more-link">
                            더보기 <i class="bi bi-chevron-right"></i>
                        </a>
                    </div>

                    <c:choose>
                    <c:when test="${not empty notices}">
                    <div class="notice-list">
                        <c:forEach var="notice" items="${notices}">
                            <a href="/notice/content?id=${notice.id}">
                                <span class="notice-title">${notice.title}</span>
                                <small class="notice-date">${notice.createdAt}</small>
                            </a>
                        </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="no-notice text-muted">공지사항이 없습니다.</div>
                        </c:otherwise>
                        </c:choose>
                    </div>

                </div>
            </div>


    </main>

</section>
<!-- 컨텐츠 끝 -->


<%@ include file="/WEB-INF/views/common/modal.jsp" %>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
