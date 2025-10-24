document.addEventListener("DOMContentLoaded", function () {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    //=== 좋아요 해제 === //
    const likeModal = document.getElementById('likeModal');
    if (likeModal) {
        const likeModalBody = likeModal.querySelector('.modal-body');
        const likeModalTitle = likeModal.querySelector('.modal-title');
        const likeConfirmBtn = likeModal.querySelector('.btn-main');
        let likeCurrentLectureId = null;

        const bsLikeModal = new bootstrap.Modal(likeModal);

        document.querySelectorAll(".btn-unlike").forEach(button => {
            button.addEventListener("click", function () {
                likeCurrentLectureId = this.dataset.lectureId;
                likeModalTitle.textContent = "좋아요 해제";
                likeModalBody.innerHTML = `선택하신 강의를 좋아요 목록에서<br><strong>정말 해제하시겠습니까?</strong>`;
                likeConfirmBtn.textContent = "해제";
                bsLikeModal.show();
            });
        });

        likeConfirmBtn.addEventListener("click", function () {
            if (!likeCurrentLectureId) return;

            fetch("/mypage/like/delete", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                    [csrfHeader]: csrfToken
                },
                body: new URLSearchParams({lectureId: likeCurrentLectureId}).toString()
            })
                .then(res => {
                    if (!res.ok) throw new Error(res.statusText);
                    return res.json();
                })
                .then(data => {
                    if (data.success) {
                        likeModalBody.innerHTML = "좋아요가 해제되었습니다.";
                        likeConfirmBtn.textContent = "닫기";
                        likeConfirmBtn.onclick = () => window.location.reload();
                    } else {
                        likeModalBody.innerHTML = "실패: " + data.message;
                    }
                })
                .catch(err => {
                    likeModalBody.innerHTML = "에러 발생: " + err.message;
                });

        });
    }

    //=== 예약 취소 === //
    //=== 예약 취소 === //
    const reserveModal = document.getElementById('reserveModal');
    if (reserveModal) {
        const reserveModalBody = reserveModal.querySelector('.modal-body');
        const reserveModalTitle = reserveModal.querySelector('.modal-title');
        const reserveConfirmBtn = reserveModal.querySelector('.btn-main');
        let reserveCurrentLectureId = null;

        const bsReserveModal = new bootstrap.Modal(reserveModal);

        // 예약 취소 버튼 클릭
        document.querySelectorAll(".btn-unreserve").forEach(button => {
            button.addEventListener("click", function () {
                reserveCurrentLectureId = this.dataset.lectureId;
                console.log('선택된 lectureId:', reserveCurrentLectureId); // 여기 찍히는지 확인

                reserveModalTitle.textContent = "예약 취소";
                reserveModalBody.innerHTML = `선택하신 강의 예약을<br><strong>정말 취소하시겠습니까?</strong>`;
                reserveConfirmBtn.textContent = "취소";
                bsReserveModal.show();
            });
        });

        // 확인 버튼 클릭
        reserveConfirmBtn.addEventListener("click", function () {
            console.log('확인 버튼 클릭됨');
            console.log('현재 lectureId:', reserveCurrentLectureId);

            if (!reserveCurrentLectureId) {
                alert('lectureId가 없습니다!');
                return;
            }

            console.log('fetch 시작');

            fetch("/mypage/lecture/delete", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                    [csrfHeader]: csrfToken
                },
                body: new URLSearchParams({lectureId: reserveCurrentLectureId}).toString()
            })
                .then(res => {
                    console.log('응답 받음, status:', res.status);
                    if (!res.ok) throw new Error(res.statusText);
                    return res.json();
                })
                .then(data => {
                    console.log('응답 데이터:', data);
                    alert('성공: ' + JSON.stringify(data));
                    window.location.reload();
                })
                .catch(err => {
                    console.error('에러 발생:', err);
                    alert('에러: ' + err.message);
                });
        });
    }

    //=== 리뷰 모달 ===//
    const reviewModal = document.getElementById('reviewModal');
    const modalReviewContent = document.getElementById('modalReviewContent');
    const modalReviewStar = document.getElementById('modalReviewStar');
    const reviewMoreBtn = document.getElementById('reviewMoreBtn');
    let currentLectureId = null;

    const bsReviewModal = new bootstrap.Modal(reviewModal);

    document.querySelectorAll(".review-card").forEach(card => {
        card.addEventListener("click", function () {
            const content = this.dataset.reviewContent;
            const star = this.dataset.reviewStar;
            currentLectureId = this.dataset.lectureId;

            modalReviewContent.textContent = content;
            modalReviewStar.textContent = `별점: ${star} ★`;

            bsReviewModal.show();
        });
    });

    // “더보기” → 해당 강좌 상세페이지로 이동
    reviewMoreBtn.addEventListener("click", function () {
        if (currentLectureId) {
            window.location.href = `/lecture/content/${currentLectureId}#review`;
        }
    });
    document.querySelectorAll(".review-card").forEach(card => {
        card.addEventListener("click", function () {
            currentLectureId = this.dataset.lectureId;
            console.log("lectureId:", currentLectureId); // ← 여기에 값 찍히면 완벽
            bsReviewModal.show();
        });
    });

});
