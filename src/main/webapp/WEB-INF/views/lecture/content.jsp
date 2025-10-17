<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- 강좌 상세 페이지 시작 -->
<section id="sub" class="container" style="margin-top: 100px;">
    <!-- 강좌 요약 -->
    <div class="p-4 p-md-5 mb-4 rounded text-body-emphasis bg-body-secondary">
        <div class="row align-items-center">
            <!-- 왼쪽: 텍스트 -->
            <div class="col-lg-7 px-4">
                <h1 class="display-5 fw-bold mb-3">${lecture.title}</h1>

                <p class="lead mb-2">강사: <strong>${lecture.tutorName}</strong></p>
                <p class="lead mb-2">
                    신청기간:
                    <strong><fmt:formatDate value="${lecture.reservationStart}" pattern="yyyy-MM-dd" /></strong> ~
                    <strong><fmt:formatDate value="${lecture.reservationEnd}" pattern="yyyy-MM-dd" /></strong>
                </p>
                <p class="lead mb-2">
                    강좌기간:
                    <strong><fmt:formatDate value="${lecture.lectureStart}" pattern="yyyy-MM-dd" /></strong> ~
                    <strong><fmt:formatDate value="${lecture.lectureEnd}" pattern="yyyy-MM-dd" /></strong>
                </p>
                <p class="lead mb-2">
                    신청인원: <strong>${lecture.reservationCount}/${lecture.totalCount}</strong>
                </p>

                <h2 class="mt-4 text-primary fw-bold">수강비: ${lecture.price}원</h2>

                <!-- 좋아요 + 결제하기 버튼 -->
                <div class="mt-3">
                    <button id="likeBtn"
                            type="button"
                            class="btn btn-outline-danger me-2"
                            data-lecture-id="${lecture.lectureId}">
                        🤍좋아요
                    </button>

                    <c:choose>
                        <%-- 무료 강의인 경우 --%>
                        <c:when test="${lecture.price == 0}">
                            <form action="/lecture/enroll/${lecture.lectureId}" method="post" style="display:inline;">
                                <button type="submit" class="btn btn-success text-white">바로 수강하기</button>
                            </form>
                        </c:when>

                        <%-- 유료 강의인 경우 --%>
                        <c:otherwise>
                            <a href="#" class="btn btn-warning text-white">
                                결제하기
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>


            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const likeBtn = document.getElementById("likeBtn");
                    const lectureId = likeBtn.getAttribute("data-lecture-id");

                    // 페이지 처음 로드 시 좋아요 상태 확인
                    fetch(`/lecture/like/check/${lectureId}`)
                        .then(res => res.json())
                        .then(isLiked => {
                            if (isLiked) {
                                likeBtn.innerText = "❤️좋아요";
                                likeBtn.classList.add("active");
                            } else {
                                likeBtn.innerText = "🤍좋아요";
                                likeBtn.classList.remove("active");
                            }
                        });

                    // 버튼 클릭 시 좋아요 토글
                    likeBtn.addEventListener("click", function () {
                        fetch(`/lecture/like/${lectureId}`, { method: "POST" })
                            .then(res => res.text())
                            .then(status => {
                                if (status === "liked") {
                                    likeBtn.innerText = "❤️좋아요";
                                } else {
                                    likeBtn.innerText = "🤍좋아요";
                                }
                            })
                            .catch(err => console.error("좋아요 오류:", err));
                    });
                });
            </script>



            <!-- 오른쪽: 썸네일 -->
            <div class="col-lg-5 text-center">
                <img src="/img/lecture/lecture_${lecture.lectureId}.jpg"
                     onerror="this.src='/img/common/no-image.png';"
                     alt="lecture thumbnail"
                     class="img-fluid rounded shadow-sm border"
                     style="max-height: 280px; object-fit: cover;">
            </div>
        </div>
    </div>

    <!-- 탭 메뉴 -->
    <ul class="nav nav-underline justify-content-between" id="lectureTab" role="tablist">
        <li class="nav-item"><a class="nav-link active" data-bs-toggle="tab" href="#intro">강좌소개</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#team">강좌운영진</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#list">강의목록</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#review">수강후기</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#material">학습자료실</a></li>
    </ul>

    <!-- 탭 내용 -->
    <div class="tab-content mt-3" id="lectureTabContent">

        <!-- 강좌소개 -->
        <div class="tab-pane fade show active" id="intro">
            <h3>강좌 소개</h3>
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
                <li class="list-group-item">학습단계: 입문</li>
                <li class="list-group-item">수준: 초급</li>
                <li class="list-group-item">별점: ⭐⭐⭐⭐☆ (추후 적용)</li>
            </ul>
            <h4 class="mt-3">${lecture.content}</h4>
        </div>

        <!-- 강좌운영진 -->
        <div class="tab-pane fade" id="team">
            <h3>강좌 운영진</h3>
            <div class="col-md-6">
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
        <div class="tab-pane fade" id="list">
            <h3>강의 목록</h3>
            <article class="blog-post">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>주차</th>
                        <th>학습내용</th>
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
        <div class="tab-pane fade" id="review">
            <h3>수강 후기</h3>

            <!-- 평균 별점 -->
            <div class="container py-4">
                <div class="p-5 mb-4 bg-body-tertiary rounded-3">
                    <div class="container-fluid py-5 text-center">
                        <h1 class="display-5 fw-bold">
                            평균 <fmt:formatNumber value="${avgStar}" type="number" maxFractionDigits="1" /> ⭐
                        </h1>
                        <p class="col-md-8 fs-4 mx-auto">${reviews.size()}개의 수강평</p>
                    </div>
                </div>
            </div>

            <!-- 후기 목록 -->
            <c:forEach var="review" items="${reviews}">
                <div class="col-md-12 mb-3">
                    <div class="h-100 p-5 bg-body-tertiary border rounded-3">
                        <h4>${review.reservation.user.name}</h4>
                        <h5><fmt:formatDate value="${review.createdAt}" pattern="yyyy.MM.dd" /></h5>

                        <h4>
                            <c:forEach var="i" begin="1" end="5">
                                <c:choose>
                                    <c:when test="${i <= review.reviewStar}">⭐</c:when>
                                    <c:otherwise>☆</c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </h4>

                        <h4><strong>${review.reviewContent}</strong></h4>

                        <button class="btn btn-outline-danger btn-sm"
                                type="button"
                                data-bs-toggle="modal"
                                data-bs-target="#reportModal"
                                data-review-id="${review.reviewUId}">
                            🚨 신고
                        </button>
                    </div>
                </div>
            </c:forEach>


            <!-- 후기 등록 : js랑 css 코드 나중에 빼기!! 나중에 꾸밀 때 폴더 따로 만들어서 빼기!!! -->
            <div class="col-md-12 mt-4">
                <form action="/lecture/content/${lecture.lectureId}/review" method="post">
                    <div class="h-100 p-5 bg-body-secondary border rounded-3">
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
                        <div class="mb-3">
                            <textarea class="form-control" name="reviewContent" rows="5" placeholder="수강 후기를 입력하세요"></textarea>
                        </div>

                        <button class="btn btn-primary btn-lg" type="submit">등록</button>
                    </div>
                </form>
            </div>

            <!-- JS -->
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const stars = document.querySelectorAll(".bi-star-fill");
                    const input = document.querySelector("#selectedStar");

                    stars.forEach((star, index) => {
                        star.addEventListener("mouseover", () => {
                            for (let i = 0; i < stars.length; i++) {
                                stars[i].classList.toggle("on", i <= index);
                            }
                        });

                        star.addEventListener("click", () => {
                            input.value = index + 1;
                            for (let i = 0; i < stars.length; i++) {
                                stars[i].classList.toggle("active", i <= index);
                            }
                        });

                        star.addEventListener("mouseleave", () => {
                            stars.forEach((s) => s.classList.remove("on"));
                        });
                    });
                });
            </script>

            <!-- CSS -->
            <style>

                :root {
                    --color-light-gray: #ccc;
                    --color-point: #f1c40f;
                    --color-main: #0d6efd;
                    --color-white: #ffffff;
                    --color-black: #000000;
                    --color-white-dark: #f9f9f9;
                    --color-main-dark: #0b5ed7;
                    --border-black: 1px solid #000;
                }
                .star-wrap {
                    margin: 1.5rem 0;
                    display: flex;
                    justify-content: center;
                    gap: 0.5rem;
                }
                .bi-star-fill {
                    color: var(--color-light-gray, #ccc);
                    transition: 0.15s ease;
                    font-size: 2rem;
                    background: none;
                    border: none;
                }
                .bi-star-fill:hover {
                    color: var(--color-point, #f1c40f);
                    transform: scale(1.2);
                }
                .bi-star-fill.on {
                    color: var(--color-point, #f1c40f);
                }
                .bi-star-fill.active {
                    color: var(--color-point, #f1c40f);
                    transform: scale(1.1);
                }
            </style>
        </div>


        <!-- 학습자료실 -->
        <div class="tab-pane fade" id="material">
            <h3>학습 자료실</h3>

            <div class="container py-4">
                <div class="p-5 mb-4 bg-body-tertiary rounded-3 shadow-sm">
                    <div class="container-fluid">

                        <c:choose>
                            <c:when test="${not empty attachments}">
                                <ul class="list-group">
                                    <c:forEach var="file" items="${attachments}">
                                        <li class="list-group-item d-flex justify-content-between align-items-center">
                                            <div>
                                                <strong>${file.fileName}</strong>
                                            </div>
                                            <a href="${file.fileUrl}" class="btn btn-outline-primary btn-sm" download>다운로드</a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>

                            <c:otherwise>
                                <p class="text-muted text-center mt-3">등록된 학습 자료가 없습니다.</p>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </div>
            </div>
        </div>

        <!-- 신고 모달 -->
        <div class="modal fade" id="reportModal" tabindex="-1" aria-labelledby="reportModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">

                    <form action="/lecture/content/${lecture.lectureId}/report" method="post">
                        <input type="hidden" name="reviewUId" id="reportReviewId">

                        <div class="modal-header">
                            <h5 class="modal-title" id="reportModalLabel">리뷰 신고</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="닫기"></button>
                        </div>

                        <div class="modal-body">
                            <p>신고 사유</p>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="reportReason" id="abuse" value="부적절한 언어 사용">
                                <label class="form-check-label" for="abuse">부적절한 언어 사용</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="reportReason" id="spam" value="스팸/광고성 내용">
                                <label class="form-check-label" for="spam">스팸/광고성 내용</label>
                            </div>
                            <div class="form-check">
                                <input class="form-check-input" type="radio" name="reportReason" id="etc" value="기타">
                                <label class="form-check-label" for="etc">기타</label>
                            </div>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
                            <button type="submit" class="btn btn-primary">신고하기</button>
                        </div>
                    </form>

                </div>
            </div>
        </div>


        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const reportModal = document.getElementById("reportModal");
                reportModal.addEventListener("show.bs.modal", function (event) {
                    const button = event.relatedTarget; // 클릭한 신고 버튼
                    const reviewId = button.getAttribute("data-review-id");
                    const input = document.getElementById("reportReviewId");
                    input.value = reviewId; // hidden input에 값 넣기
                });
            });
        </script>





    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
