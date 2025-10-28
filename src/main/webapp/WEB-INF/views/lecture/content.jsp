<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/modal.jsp" %>
<link rel="stylesheet" href="/css/main.css" />
<link rel="stylesheet" href="/css/lectureContent.css">
<script src="https://js.tosspayments.com/v2/standard"></script>

<!-- 강좌 상세 페이지 시작 -->
<section id="sub" class="container" style="margin-top: 100px;">
    <!-- 강좌 요약 -->
    <div class="border rounded-3 info-banner">
        <div class="row align-items-center">
            <!-- 왼쪽: 텍스트 -->
            <div class="mb-3">
                <a href="/lecture/list" class="btn-main">
                    ← 목록으로
                </a>
            </div>
            <div class="d-flex justify-content-between">
                <div class="col-lg-7">
                    <h1 class="display-5 fw-bold mb-3">${lecture.title}</h1>

                    <p class="lead mb-2">강사: <strong>${lecture.tutorName}</strong></p>
                    <p class="lead mb-2">
                        신청기간:
                        <strong>${lecture.reservationStart.toLocalDate()}</strong> ~
                        <strong>${lecture.reservationEnd.toLocalDate()}</strong>
                    </p>
                    <p class="lead mb-2">
                        강좌기간:
                        <strong>${lecture.lectureStart}</strong> ~
                        <strong>${lecture.lectureEnd}</strong>
                    </p>
                    <p class="lead mb-2">
                        신청인원: <strong>${lecture.reservationCount}/${lecture.totalCount}</strong>
                    </p>

                    <h2 class="lecture-price">
                        수강비: <fmt:formatNumber value="${lecture.price}" pattern="#,###" />원
                    </h2>

                    <!-- 좋아요 + 결제하기 버튼 -->
                    <div class="mt-3">
                        <button id="likeBtn"
                                type="button"
                                class="btn btn-outline-danger me-2"
                                data-lecture-id="${lecture.lectureId}">
                            🤍
                        </button>

                        <c:choose>
                            <%-- 무료 강의 --%>
                            <c:when test="${lecture.price == 0}">
                                <c:choose>
                                    <%-- 이미 수강중 --%>
                                    <c:when test="${reservationStatus == 'CONFIRMED'}">
                                        <button type="button" class="btn-cancel" disabled>신청한 강의</button>
                                    </c:when>

                                    <%-- 무료 수강 가능 --%>
                                    <c:otherwise>
                                        <form action="/lecture/enroll/${lecture.lectureId}" method="post" style="display:inline;">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                            <button type="submit" class="btn-point">바로 수강하기</button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>

                            <%-- 유료 강의 --%>
                            <c:otherwise>
                                <c:choose>
                                <%-- 강의 상태에 따른 버튼 --%>
                                    <c:when test="${lecture.status == 'CLOSED' || lecture.status == 'ENDED'}">
                                        <button type="button" class="btn-cancel" disabled>
                                            <c:choose>
                                                <c:when test="${lecture.status == 'CLOSED'}">예약 마감</c:when>
                                                <c:when test="${lecture.status == 'ENDED'}">강의 종료</c:when>
                                            </c:choose>
                                        </button>
                                    </c:when>
                                    <%-- 아직 예약 자체가 없음 (전혀 신청 전) --%>
                                    <c:when test="${empty reservationStatus}">
                                        <button id="payButton" class="btn-point" onclick="requestPayment()">결제하기</button>
                                    </c:when>

                                    <%-- 결제 대기 상태 (위젯닫힘/실패 등) --%>
                                    <c:when test="${reservationStatus == 'PENDING'}">
                                        <button class="btn-point" onclick="requestPayment()">결제 다시 시도</button>
                                    </c:when>

                                    <%-- 결제 완료됨 --%>
                                    <c:when test="${reservationStatus == 'PAID' || reservationStatus == 'CONFIRMED'}">
                                        <button type="button" class="btn-cancel" disabled>신청한 강의</button>
                                    </c:when>

                                    <%-- 환불됨 (다시 신청 가능) --%>
                                    <c:when test="${reservationStatus == 'CANCEL'}">
                                        <button id="payButton" class="btn-point" onclick="requestPayment()">환불 후 재결제하기</button>
                                    </c:when>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- 오른쪽: 썸네일 -->
                <div class="">
                    <img src="${lecture.thumbnailUrl}"
                         onerror="this.src='/img/common/no-image.png';"
                         alt="lecture thumbnail"
                         class="img-fluid rounded shadow-sm border"
                         style="max-height: 280px; object-fit: cover;">
                </div>
            </div>
        </div>
    </div>

    <!-- 탭 메뉴 -->
    <ul class="nav lecture-tab-menu sticky-top" id="lectureTab">
        <li class="nav-item"><a class="nav-link active" href="#intro">강좌소개</a></li>
        <li class="nav-item"><a class="nav-link" href="#team">강좌운영진</a></li>
        <li class="nav-item"><a class="nav-link" href="#list">강의목록</a></li>
        <li class="nav-item"><a class="nav-link" href="#review">수강후기</a></li>
    </ul>

    <!-- 강좌소개 -->
    <div id="intro" class="tab-content">
        <h4>강좌 소개</h4>
        <ul class="list-group">
            <li class="list-group-item">
                <strong>카테고리:</strong>
                <c:choose>
                    <c:when test="${lecture.category.parent != null}">
                        ${lecture.category.parent.categoryName} &gt; ${lecture.category.categoryName}
                    </c:when>
                    <c:otherwise>
                        ${lecture.category.categoryName}
                    </c:otherwise>
                </c:choose>
            </li>
            <li class="list-group-item">별점⭐:
                <fmt:formatNumber value="${avgStarMap[lecture.lectureId]}" type="number" maxFractionDigits="1" />
            </li>
        </ul>
        <h4 class="mt-3">${lecture.content}</h4>
    </div>

    <!-- 강좌운영진 -->
    <div id="team" class="tab-content">
        <h4>강좌 운영진</h4>
        <div class="">
            <div class="row g-0 border rounded overflow-hidden flex-md-row mb-4 shadow-sm h-md-250 position-relative">
                <div class="col p-4 d-flex flex-column position-static">
                    <strong class="d-inline-block mb-2 text-primary-emphasis">Livo</strong>
                    <h3 class="mb-3">${lecture.tutorName}</h3>
                    <p class="card-text mt-2 mb-0" style="line-height:1.6;">
                        ${lecture.tutorInfo}
                    </p>
                </div>
            </div>
        </div>
    </div>

    <!-- 강의목록 -->
    <div id="list" class="tab-content">
        <h4>강의 목록</h4>
        <article class="blog-post">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th><strong>주차</strong></th>
                        <th><strong>학습내용</strong></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="chapter" items="${chapters}">
                        <tr>
                            <td>${chapter.chapterOrder}주차</td>
                            <td>${chapter.chapterName}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </article>
    </div>

    <!-- 수강후기 -->
    <div id="review" class="tab-content">
        <h4>수강 후기</h4>

        <!-- 평균 별점 -->
        <div class="container py-4">
            <div class="avg-box text-center">
                <div class="avg-box-inner">
                    <h1 class="display-5 fw-bold">
                        평균
                        <fmt:formatNumber value="${avgStarMap[lecture.lectureId]}" type="number" maxFractionDigits="1" /> ⭐
                    </h1>
                    <p class="fs-4 mb-0">${reviewCountMap[lecture.lectureId]}개의 수강평</p>
                </div>
            </div>
        </div>

        <!-- 후기 등록 (CONFIRMED or PAID) -->
        <c:choose>
            <c:when test="${reservationStatus == 'CONFIRMED' || reservationStatus == 'PAID'}">
                <div class="col-md-12 my-5">
                    <form id="reviewForm" action="/lecture/content/${lecture.lectureId}/review" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" id="reviewUId" value="">

                        <div class="p-4 bg-body-secondary border rounded-3">
                            <!-- 별점 버튼 -->
                            <div class="star-wrap mb-3">
                                <button type="button" class="bi bi-star-fill" data-value="1"></button>
                                <button type="button" class="bi bi-star-fill" data-value="2"></button>
                                <button type="button" class="bi bi-star-fill" data-value="3"></button>
                                <button type="button" class="bi bi-star-fill" data-value="4"></button>
                                <button type="button" class="bi bi-star-fill" data-value="5"></button>
                            </div>
                            <input type="hidden" name="reviewStar" id="selectedStar" value="0">

                            <h4>내용입력</h4>
                            <textarea class="form-control mb-3" id="reviewContent" name="reviewContent" rows="4"
                                      placeholder="수강 후기를 입력하세요"></textarea>

                            <div class="d-flex justify-content-center gap-2">
                                <button type="submit" class="btn-main">등록</button>
                            </div>

                        </div>
                    </form>
                </div>
            </c:when>
        </c:choose>

        <!-- 후기 목록 -->
        <div id="reviewList">
            <c:forEach var="review" items="${reviews}">
                <div class="col-md-12 mb-3">
                    <div class="h-100 review-box" data-review-id="${review.reviewUId}">
                        <h4>${review.userName}</h4>
                        <h5>
                            ${review.createdAt}
                            <c:if test="${review.edited}">
                                <span class="text-muted small">(수정)</span>
                            </c:if>
                        </h5>

                        <h4>
                            <c:forEach var="i" begin="1" end="5">
                                <c:choose>
                                    <c:when test="${i <= review.reviewStar}">⭐</c:when>
                                    <c:otherwise>☆</c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </h4>

                        <h4>
                            <c:choose>
                                <c:when test="${review.blocked}">
                                    <span class="text-muted fst-italic">🚫 신고된 리뷰입니다.</span>
                                </c:when>
                                <c:otherwise>
                                    <strong>${review.reviewContent}</strong>
                                </c:otherwise>
                            </c:choose>
                        </h4>

                        <!-- 신고/수정/삭제 버튼 -->
                        <div class="d-flex gap-2 mt-2">
                            <c:choose>
                                <c:when test="${isLoggedIn}">
                                    <!-- 로그인 O → 본인 리뷰인지 검사 -->
                                    <c:choose>
                                        <c:when test="${review.blocked}">
                                            <button class="btn btn-outline-secondary btn-sm" disabled>신고된 리뷰</button>
                                        </c:when>
                                        <%-- 민영 추가: 이미 신고한 리뷰일 경우 --%>
                                        <c:when test="${reportedIds.contains(review.reviewUId)}">
                                            <button class="btn btn-secondary btn-sm" disabled>검토중</button>
                                        </c:when>
                                        <%-- 본인 리뷰가 아닌 경우: 신고 버튼 --%>
                                        <c:when test="${review.userEmail ne loggedInUserEmail}">
                                            <button class="btn btn-outline-danger btn-sm"
                                                    type="button"
                                                    data-bs-toggle="modal"
                                                    data-bs-target="#reportModal"
                                                    data-review-id="${review.reviewUId}">
                                                🚨 신고
                                            </button>
                                        </c:when>

                                        <c:otherwise>
                                            <!-- 본인 리뷰: 수정 + 삭제 -->
                                            <button class="btn btn-outline-secondary btn-sm" disabled>
                                                나의 후기
                                            </button>
                                            <button class="btn btn-outline-primary btn-sm"
                                                    type="button"
                                                    onclick="editReview(${review.reviewUId})">
                                                수정
                                            </button>
                                            <button class="btn btn-outline-danger btn-sm"
                                                    type="button"
                                                    onclick="deleteReview(${review.reviewUId})">
                                                삭제
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>

                                <c:otherwise>
                                    <!-- 로그인 X → 로그인 페이지로 이동 -->
                                    <a href="/auth/login" class="btn btn-outline-danger btn-sm">
                                        🚨 신고
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- 더보기 버튼 -->
        <c:if test="${reviewCount > 5}">
            <div class="text-center mt-4">
                <button id="loadMoreBtn"
                        type="button"
                        class="btn-main"
                        data-page="1"
                        data-lecture-id="${lecture.lectureId}">
                    더보기 ▼
                </button>
            </div>
        </c:if>

    <!-- 🚨 리뷰 신고 모달 -->
    <div class="modal fade" id="reportModal" tabindex="-1" aria-labelledby="reportModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <form action="/lecture/content/${lecture.lectureId}/report" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <input type="hidden" name="reviewUId" id="reportReviewId">

                    <!-- 모달 헤더 (공용 디자인 유지) -->
                    <div class="modal-header">
                        <h5 class="modal-title" id="reportModalLabel">리뷰 신고</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
                    </div>

                    <!-- 모달 본문 (공용 구조 유지) -->
                    <div class="modal-body">
                        <p class="mb-3">신고 사유를 선택해주세요.</p>
                        <div class="form-check mb-2">
                            <input class="form-check-input" type="radio" name="reportReason" id="abuse" value="부적절한 언어 사용" required>
                            <label class="form-check-label" for="abuse">부적절한 언어 사용</label>
                        </div>
                        <div class="form-check mb-2">
                            <input class="form-check-input" type="radio" name="reportReason" id="spam" value="스팸/광고성 내용">
                            <label class="form-check-label" for="spam">스팸/광고성 내용</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="reportReason" id="etc" value="기타">
                            <label class="form-check-label" for="etc">기타</label>
                        </div>

                        <!-- 기타 직접입력 칸 (기본 숨김) -->
                        <div id="etcInputBox" class="mt-3" style="display:none;">
                            <textarea class="form-control" name="customReason" rows="3" placeholder="기타 사유를 입력해주세요."></textarea>
                        </div>
                    </div>

                    <!-- 모달 하단 버튼 (공통 버튼 스타일 적용) -->
                    <div class="modal-footer">
                        <button type="button" class="btn-cancel" data-bs-dismiss="modal">취소</button>
                        <button type="submit" class="btn-main">신고하기</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<script>
    const csrfToken = "${_csrf.token}";
</script>

<script>
    // 로그인 유저 이메일 JSP에서 JS 변수로 넘기기
    const userEmail = "${loggedInUserEmail != null ? loggedInUserEmail : ''}";
    const lectureId = ${lecture.lectureId};
    const amount = ${lecture.price};
    const lectureName = "${lecture.title}";

    console.log("강의이름:", lectureName);
    console.log("로그인된 사용자 이메일:", userEmail);
</script>

<script src="/js/modal.js"></script>
<script src="/js/lectureContent.js"></script>
<script src="/js/payment.js"></script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>