// =====================================================
// ❤️ 좋아요 기능 (Lecture Like Section)
// =====================================================
document.addEventListener("DOMContentLoaded", function () {

    const likeBtn = document.getElementById("likeBtn");
    if (!likeBtn) return; // 버튼 없으면 종료 (안전하게)

    const lectureId = likeBtn.getAttribute("data-lecture-id");

    // ✅ CSRF 토큰 가져오기 (Spring이 쿠키로 내려줌)
    const csrfCookie = document.cookie.split("; ").find(row => row.startsWith("XSRF-TOKEN="));
    const csrfToken = csrfCookie ? csrfCookie.split("=")[1] : null;

    console.log("[DEBUG] XSRF-TOKEN from cookie:", csrfToken);

    // ✅ 페이지 처음 로드 시 좋아요 상태 확인
    fetch(`/lecture/like/check/${lectureId}`, { credentials: "include" })
        .then(res => res.json())
        .then(isLiked => {
            likeBtn.innerText = isLiked ? "❤️좋아요" : "🤍좋아요";
            likeBtn.classList.toggle("active", isLiked);
        })
        .catch(err => console.error("좋아요 상태 확인 오류:", err));

    // ✅ 좋아요 토글 (CSRF 쿠키 포함해서 전송)
    likeBtn.addEventListener("click", function () {
        if (!csrfToken) {
            alert("CSRF 토큰이 없습니다. 새로고침 후 다시 시도해주세요.");
            return;
        }

        fetch(`/lecture/like/${lectureId}`, {
            method: "POST",
            headers: {
                "X-XSRF-TOKEN": csrfToken
            },
            credentials: "include"
        })
            .then(res => {
                if (res.status === 403) {
                    throw new Error("403 Forbidden: CSRF 토큰 문제");
                }
                return res.text();
            })
            .then(status => {
                if (status === "unauthorized") {
                    alert("로그인이 필요합니다.");
                    window.location.href = "/auth/login";
                } else if (status === "liked") {
                    likeBtn.innerText = "❤️좋아요";
                    likeBtn.classList.add("active");   // ✅ 추가
                } else if (status === "unliked") {
                    likeBtn.innerText = "🤍좋아요";
                    likeBtn.classList.remove("active"); // ✅ 추가
                } else {
                    console.warn("예상치 못한 응답:", status);
                }
            })
            .catch(err => console.error("좋아요 오류:", err));
    });


    // =====================================================
    // ⭐ 리뷰 별점 기능 (Review Star Rating)
    // =====================================================
    const stars = document.querySelectorAll(".bi-star-fill");
    const input = document.querySelector("#selectedStar");

    if (stars.length > 0 && input) {
        stars.forEach((star, index) => {
            // 마우스 올릴 때
            star.addEventListener("mouseover", () => {
                for (let i = 0; i < stars.length; i++) {
                    stars[i].classList.toggle("on", i <= index);
                }
            });

            // 클릭해서 선택할 때
            star.addEventListener("click", () => {
                input.value = index + 1;
                for (let i = 0; i < stars.length; i++) {
                    stars[i].classList.toggle("active", i <= index);
                }
            });

            // 마우스가 벗어났을 때
            star.addEventListener("mouseleave", () => {
                stars.forEach((s) => s.classList.remove("on"));
            });
        });
    }

    // =====================================================
    // ✏️ 후기 등록 & 수정 통합
    // =====================================================
    const reviewForm = document.getElementById("reviewForm");
    if (reviewForm) {
        reviewForm.addEventListener("submit", async function (e) {
            e.preventDefault(); // 항상 JS로 제어

            const content = document.getElementById("reviewContent").value.trim();
            const star = parseInt(document.getElementById("selectedStar").value);
            const reviewUIdInput = document.getElementById("reviewUId");
            const reviewUId = reviewUIdInput ? reviewUIdInput.value : null; // ✅ 수정모드 확인
            const csrfToken = document.querySelector("input[name='_csrf']").value;
            const lectureId = reviewForm.action.split("/content/")[1].split("/review")[0];

            if (content === "") {
                alert("후기 내용을 입력해주세요!");
                return;
            }
            if (star === 0) {
                alert("별점을 선택해주세요!");
                return;
            }

            const reviewData = {
                reviewStar: star,
                reviewContent: content
            };

            let url, method, body;
            if (reviewUId) {
                // ✅ 수정모드
                url = `/lecture/review/${reviewUId}`;
                method = "PUT";
                body = JSON.stringify(reviewData);
            } else {
                // ✅ 신규 등록
                url = `/lecture/content/${lectureId}/review`;
                method = "POST";
                body = new URLSearchParams(reviewData);
            }

            const response = await fetch(url, {
                method: method,
                headers: {
                    "Content-Type": method === "POST" ? "application/x-www-form-urlencoded" : "application/json",
                    "X-XSRF-TOKEN": csrfToken
                },
                body: body
            });

            if (response.ok) {
                if (reviewUId) {
                    showCommonModal("리뷰 수정 완료", "리뷰가 성공적으로 수정되었습니다.", "확인", false);
                    window.location.href = window.location.pathname + "?reviewUpdated=success";
                } else {
                    showCommonModal("리뷰 등록 완료", "후기가 성공적으로 등록되었습니다.", "확인", false);
                    window.location.href = window.location.pathname + "?reviewed=success";
                }
            } else {
                showCommonModal("오류 발생", "리뷰 저장 중 문제가 발생했습니다.", "확인", false);
            }
        });
    }


    // =====================================================
    // 📖 후기 더보기 기능 (Load More Reviews)
    // =====================================================
    const loadMoreBtn = document.getElementById("loadMoreBtn");
    if (loadMoreBtn) {
        loadMoreBtn.addEventListener("click", function () {
            const lectureId = this.dataset.lectureId;
            let page = parseInt(this.dataset.page);

            fetch(`/lecture/content/${lectureId}/reviews?page=${page}`)
                .then(res => res.json())
                .then(data => {
                    const container = document.getElementById("reviewList");

                    // ✅ 컨트롤러에서 내려준 로그인 정보 꺼내기
                    const { isLoggedIn, loggedInUserEmail } = data;

                    // ✅ 후기 목록 추가
                    data.content.forEach(r => {
                        const stars = "⭐".repeat(r.reviewStar) + "☆".repeat(5 - r.reviewStar);
                        const reviewContentHtml = r.blocked
                            ? `<span class="text-muted fst-italic">🚫 신고된 리뷰입니다.</span>`
                            : `<strong>${r.reviewContent}</strong>`;

                        // 🚨 신고 / 수정 / 삭제 버튼 조건 로직
                        let actionBtns = "";

                        // 🚨 신고 버튼 조건 로직
                        if(r.blocked){
                            actionBtns = `<button class="btn btn-outline-secondary btn-sm" disabled>신고된 리뷰</button>`;
                        }
                        else if (isLoggedIn) {
                            if (r.userEmail === loggedInUserEmail) {
                                // 본인 후기 → 수정/삭제 표시
                                actionBtns = `
                                <div class="d-flex gap-2 mt-2">
                                    <button class="btn btn-outline-secondary btn-sm" disabled>나의 후기</button>
                                    <button class="btn btn-outline-primary btn-sm"
                                            type="button"
                                            onclick="editReview(${r.reviewUId})">
                                        수정
                                    </button>
                                    <button class="btn btn-outline-danger btn-sm"
                                            type="button"
                                            onclick="deleteReview(${r.reviewUId})">
                                        삭제
                                    </button>
                                </div>
                            `;
                            } else {
                                // 다른 사람 후기 → 신고만 가능
                                actionBtns = `
                                <div class="d-flex gap-2 mt-2">
                                    <button class="btn btn-outline-danger btn-sm"
                                            type="button"
                                            data-bs-toggle="modal"
                                            data-bs-target="#reportModal"
                                            data-review-id="${r.reviewUId}">
                                        🚨 신고
                                    </button>
                                </div>
                            `;
                            }
                        } else {
                            // 로그인 X → 로그인 유도
                            actionBtns = `
                            <div class="d-flex gap-2 mt-2">
                                <a href="/auth/login" class="btn btn-outline-danger btn-sm">🚨 신고</a>
                            </div>
                        `;
                        }

                        // ✅ 후기 HTML 구성
                        const item = `
                        <div class="col-md-12 mb-3 fade-in-up">
                            <div class="h-100 p-5 bg-body-tertiary border rounded-3 shadow-sm" data-review-id="${r.reviewUId}">
                                <h4>${r.userName}</h4>
                                <h5>${r.createdAt}${r.isEdited ? ' <span class="text-muted small">(수정)</span>' : ''}</h5>
                                <h4>${stars}</h4>
                                <h4>
                                <strong>${reviewContentHtml}</strong>
                                </h4>
                                ${actionBtns}
                            </div>
                        </div>
                    `;
                        container.insertAdjacentHTML("beforeend", item);
                    });

                    // ✅ 애니메이션
                    document.querySelectorAll(".fade-in-up").forEach(el => {
                        el.style.opacity = 0;
                        el.style.transform = "translateY(20px)";
                        setTimeout(() => {
                            el.style.transition = "all 0.4s ease";
                            el.style.opacity = 1;
                            el.style.transform = "translateY(0)";
                        }, 50);
                    });

                    page++;
                    loadMoreBtn.dataset.page = page;
                    if (data.last) {
                        loadMoreBtn.style.display = "none";
                    }
                    loadMoreBtn.scrollIntoView({ behavior: "smooth", block: "center" });
                })
                .catch(err => console.error("리뷰 불러오기 오류:", err));
        });
    }


    // =====================================================
    // 🚨 리뷰 신고 모달 기능 (Report Modal Section)
    // =====================================================
    const reportModal = document.getElementById("reportModal");
    if (reportModal) {
        reportModal.addEventListener("show.bs.modal", function (event) {
            const button = event.relatedTarget;
            const reviewId = button.getAttribute("data-review-id");
            const input = document.getElementById("reportReviewId");
            if (input) {
                input.value = reviewId;
            }
        });
    }

    // =====================================================
    // 🧭 탭 클릭 시 active 유지 (Anchor Scroll Tab)
    // =====================================================
    const tabLinks = document.querySelectorAll('#lectureTab .nav-link');
    if (tabLinks.length > 0) {
        tabLinks.forEach(link => {
            link.addEventListener('click', function () {
                tabLinks.forEach(el => el.classList.remove('active'));
                this.classList.add('active');
            });
        });
    }

}); // ✅ document.addEventListener 끝


// =====================================================
// ✏️ 리뷰 수정 함수 (리뷰 카드 자체를 폼으로 변신)
// =====================================================
function editReview(reviewUId) {
    fetch(`/lecture/review/${reviewUId}`)
        .then(res => res.json())
        .then(data => {
            const card = document.querySelector(`[data-review-id="${reviewUId}"]`);
            if (!card) return;

            // 원본 HTML 저장 (수정 후 복구용)
            const originalHTML = card.innerHTML;

            // 별점 버튼 5개 생성
            let starsHTML = '';
            for (let i = 1; i <= 5; i++) {
                const active = i <= data.reviewStar ? 'active' : '';
                starsHTML += `<button type="button" class="bi bi-star-fill ${active}" data-value="${i}"></button>`;
            }

            // 수정 폼으로 교체
            card.innerHTML = `
                <form id="inlineEditForm-${reviewUId}" class="p-4 bg-body-secondary border rounded-3">
                    <div class="star-wrap mb-3">${starsHTML}</div>
                    <input type="hidden" id="editStar-${reviewUId}" value="${data.reviewStar}">
                    <textarea class="form-control mb-3" id="editContent-${reviewUId}" rows="4">${data.reviewContent}</textarea>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary btn-sm cancelEditBtn">취소</button>
                        <button type="button" class="btn btn-primary btn-sm saveEditBtn">수정 완료</button>
                    </div>
                </form>
            `;

            // ⭐ 별점 클릭 이벤트
            const stars = card.querySelectorAll(".bi-star-fill");
            const starInput = card.querySelector(`#editStar-${reviewUId}`);
            stars.forEach((s, idx) => {
                s.addEventListener("click", () => {
                    starInput.value = idx + 1;
                    stars.forEach((st, i) => {
                        st.classList.toggle("active", i <= idx);
                    });
                });
            });

            // ❌ 취소 버튼 (복구)
            card.querySelector(".cancelEditBtn").addEventListener("click", () => {
                card.innerHTML = originalHTML;
            });

            // 💾 수정 완료 버튼
            card.querySelector(".saveEditBtn").addEventListener("click", async () => {
                const reviewContent = card.querySelector(`#editContent-${reviewUId}`).value.trim();
                const reviewStar = parseInt(starInput.value);
                const csrfToken = document.querySelector("input[name='_csrf']").value;

                if (reviewContent === "" || reviewStar === 0) {
                    alert("내용을 입력해주세요!");
                    return;
                }

                const response = await fetch(`/lecture/review/${reviewUId}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        "X-XSRF-TOKEN": csrfToken
                    },
                    body: JSON.stringify({
                        reviewContent,
                        reviewStar
                    })
                });

                if (response.ok) {
                    // 페이지 새로고침 대신 쿼리파라미터 붙이기
                    window.location.href = window.location.pathname + "?reviewUpdated=success";
                } else {
                    showCommonModal("오류 발생", "리뷰 수정 중 오류가 발생했습니다.", "확인", false);
                }
            });
        })
        .catch(err => console.error("리뷰 수정 모드 전환 실패:", err));
}

