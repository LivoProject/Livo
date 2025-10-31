document.addEventListener("DOMContentLoaded", function () {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // === 공통 모달 === //
    const modal = document.getElementById('exampleModal');
    if (!modal) return;

    const modalTitle = modal.querySelector('.modal-title');
    const modalBody = modal.querySelector('.modal-body');
    let btnMain = modal.querySelector('.btn-main');
    const btnCancel = modal.querySelector('.btn-cancel');
    const bsModal = new bootstrap.Modal(modal);

    let currentAction = null;     // 현재 모달 동작 (like, reserve, review 등)
    let currentLectureId = null;  // 현재 선택된 강의 ID

    function resetBtnMain() {
        const newBtn = btnMain.cloneNode(true);
        btnMain.parentNode.replaceChild(newBtn, btnMain);
        btnMain = newBtn;
    }
    // ====== 좋아요 해제 ====== //
    document.querySelectorAll(".btn-unlike").forEach(btn => {
        btn.addEventListener("click", function () {
            currentAction = "unlike";
            currentLectureId = this.dataset.lectureId;

            modalTitle.textContent = "";
            modalBody.innerHTML = `선택하신 강의를 좋아요 목록에서<br><strong>정말 삭제하시겠습니까?</strong>`;
            btnMain.textContent = "삭제";
            bsModal.show();
        });
    });

    // ====== 예약 취소 ====== //
    document.addEventListener("click", function (e) {
        const btn = e.target.closest(".btn-unreserve");
        if (!btn) return;

        currentAction = "unreserve";
        currentLectureId = btn.dataset.lectureId;
        const price = Number(btn.dataset.price || 0);

        modalTitle.textContent = "";

        if (price > 0) {
            modalBody.innerHTML = `
            선택하신 강의 예약을 취소하면<br>
            <strong>결제 금액이 자동으로 환불</strong>됩니다.<br><br>
            정말 취소하시겠습니까?
        `;
        } else {
            modalBody.innerHTML = `
            선택하신 강의 예약을<br>
            <strong>정말 취소하시겠습니까?</strong>
        `;
        }

        btnMain.textContent = "확인";
        bsModal.show();
    });

    // ====== 리뷰 보기 ====== //
    document.querySelectorAll(".review-card").forEach(card => {
        card.addEventListener("click", function () {
            currentAction = "review";
            currentLectureId = this.dataset.lectureId;
            const content = this.dataset.reviewContent;
            const star = this.dataset.reviewStar;

            modalTitle.textContent = ``;
            modalBody.innerHTML = `
                <p>${content}</p>
                <p class="text-muted">⭐ ${star}점</p>
            `;
            btnMain.textContent = "더보기";
            bsModal.show();
        });
    });

    // === 공통 모달 확인 버튼 === //
    btnMain.addEventListener("click", function () {
        if (!currentAction) return;
        resetBtnMain();
        // --- 좋아요 해제 처리 --- //
        if (currentAction === "unlike") {
            fetch("/mypage/like/delete", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                    [csrfHeader]: csrfToken
                },
                body: new URLSearchParams({ lectureId: currentLectureId }).toString()
            })
                .then(res => res.json())
                .then(data => {
                    modalBody.innerHTML = data.success
                        ? "좋아요가 삭제되었습니다."
                        : "실패: " + data.message;
                    btnMain.textContent = "닫기";
                    btnMain.onclick = () => window.location.reload();
                })
                .catch(err => {
                    modalBody.innerHTML = "에러 발생: " + err.message;
                });
        }

        // --- 예약 취소 처리 --- //
        else if (currentAction === "unreserve") {

            fetch("/mypage/lecture/delete", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                    [csrfHeader]: csrfToken
                },
                body: new URLSearchParams({ lectureId: currentLectureId }).toString()
            })
                .then(res => res.json())
                .then(data => {
                    modalBody.innerHTML = data.success
                        ? "예약이 취소되었습니다."
                        : "실패: " + data.message;
                    btnMain.textContent = "닫기";
                    btnMain.onclick = () => window.location.reload();
                })
                .catch(err => {
                    modalBody.innerHTML = "에러 발생: " + err.message;
                });
        }

        // --- 리뷰 더보기 --- //
        else if (currentAction === "review") {
            if (currentLectureId) {
                window.location.href = `/lecture/content/${currentLectureId}#review`;
            }
        }
    });

    // 취소 시 초기화
    modal.addEventListener("hidden.bs.modal", () => {
        currentAction = null;
        currentLectureId = null;
    });
});
