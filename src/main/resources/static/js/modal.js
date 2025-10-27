// =====================================================
// 💬 공통 모달 표시 함수 (modal.js)
// =====================================================
function showCommonModal(title, message, confirmText = "확인", showCancel = false) {
    const modalEl = document.getElementById("exampleModal");
    if (!modalEl) {
        console.warn("❗ common modal (#exampleModal)을 찾을 수 없습니다.");
        return;
    }

    const modal = new bootstrap.Modal(modalEl);

    // 제목, 내용, 버튼 텍스트 세팅
    modalEl.querySelector(".modal-title").innerText = title;
    modalEl.querySelector(".modal-body").innerHTML = message;
    modalEl.querySelector(".btn-main").innerText = confirmText;

    // 취소 버튼 표시 여부 설정
    const cancelBtn = modalEl.querySelector(".btn-cancel");
    if (cancelBtn) {
        cancelBtn.style.display = showCancel ? "inline-block" : "none";
    }

    modal.show();

    const confirmBtn = modalEl.querySelector(".btn-main");

// ✅ 확인 버튼 클릭 시 페이지 이동
    confirmBtn.onclick = () => {
        modal.hide();

        if (title === "신고 완료" || title === "리뷰 등록 완료") {
            window.location.hash = "#review";

        } else if (title === "리뷰 수정 완료") {
            window.location.hash = "#review";

        } else if (title === "리뷰 삭제 완료") {
            // URL 파라미터 제거
            const cleanUrl = window.location.origin + window.location.pathname;
            window.history.replaceState({}, document.title, cleanUrl);

            // 새로고침
            location.reload();

        } else if (title === "수강 신청 완료") {
            window.location.href = "/mypage/lecture";
        }
    };
}

// =====================================================
// 공통: 강의신청, 신고, 리뷰 등록 완료 감지 후 모달 표시
// =====================================================
document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);

    // 🚨 신고 완료
    if (urlParams.get("reported") === "success") {
        showCommonModal(
            "신고 완료",
            "신고가 정상적으로 접수되었습니다.<br>관리자가 검토 후 조치할 예정입니다.",
            "확인",
            false
        );
    }

    // ⭐ 리뷰 등록 완료
    if (urlParams.get("reviewed") === "success") {
        showCommonModal(
            "리뷰 등록 완료",
            "후기가 성공적으로 등록되었습니다.",
            "확인",
            false
        );
    }

    // 🎓 수강 신청 완료 (무료 강의)
    if (urlParams.get("enrolled") === "success") {
        showCommonModal(
            "수강 신청 완료",
            "무료 강의 수강 신청이 완료되었습니다.<br>마이페이지에서 수강 내역을 확인할 수 있습니다.",
            "마이페이지 이동",
            false
        );
    }

    // 리뷰 수정 완료
    if (urlParams.get("reviewUpdated") === "success") {
        showCommonModal(
            "리뷰 수정 완료",
            "리뷰가 성공적으로 수정되었습니다.",
            "확인",
            false
        );
    }
});


// =====================================================
// ✏️ 신고 모달에서 '기타' 선택 시 직접입력칸 토글
// =====================================================
document.addEventListener("DOMContentLoaded", function () {
    const etcRadio = document.getElementById("etc");
    const etcInputBox = document.getElementById("etcInputBox");
    const reasonRadios = document.querySelectorAll("input[name='reportReason']");

    reasonRadios.forEach(radio => {
        radio.addEventListener("change", () => {
            if (etcRadio.checked) {
                etcInputBox.style.display = "block";
            } else {
                etcInputBox.style.display = "none";
                etcInputBox.querySelector("textarea").value = ""; // 입력 내용 초기화
            }
        });
    });
});


// =====================================================
// 🗑️ 리뷰 삭제 함수 (공통 모달 사용)
// =====================================================
function deleteReview(reviewUId) {
    showCommonModal(
        "리뷰 삭제",
        "선택한 리뷰를 삭제하시겠습니까?",
        "삭제",
        true // 취소 버튼 표시
    );

    const modalEl = document.getElementById("exampleModal");
    const confirmBtn = modalEl.querySelector(".btn-main");

    confirmBtn.onclick = () => {
        fetch(`/lecture/review/${reviewUId}`, {
            method: "DELETE",
            headers: { "X-XSRF-TOKEN": csrfToken }
        })
            .then(response => {
                if (response.ok) {
                    showCommonModal(
                        "리뷰 삭제 완료",
                        "리뷰가 성공적으로 삭제되었습니다.",
                        "확인",
                        false
                    );
                }
            });
    };
}

