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
                } else if (status === "unliked") {
                    likeBtn.innerText = "🤍좋아요";
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
    // 🚨 리뷰 신고 모달 기능 (Report Modal Section)
    // =====================================================
    const reportModal = document.getElementById("reportModal");
    if (reportModal) {
        reportModal.addEventListener("show.bs.modal", function (event) {
            const button = event.relatedTarget; // 클릭한 신고 버튼
            const reviewId = button.getAttribute("data-review-id");
            const input = document.getElementById("reportReviewId");
            if (input) {
                input.value = reviewId; // hidden input에 값 넣기
            }
        });
    }

});
