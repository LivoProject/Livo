document.addEventListener("DOMContentLoaded", function() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // ✅ 모달 직접 초기화
    const modalElement = document.getElementById('exampleModal');
    if (!modalElement) {
        console.error("모달 요소(exampleModal)를 찾을 수 없습니다. modal.jsp 포함 여부 확인!");
        return;
    }

    const bsModal = new bootstrap.Modal(modalElement);
    const modalTitle = modalElement.querySelector(".modal-title");
    const modalBody = modalElement.querySelector(".modal-body");
    const btnMain = modalElement.querySelector(".btn-main");

    // ✅ 환불 버튼 클릭
    document.querySelectorAll(".btn-refund").forEach(button => {
        button.addEventListener("click", function () {
            const paymentKey = this.dataset.paymentKey;

            // 모달 구성
            modalTitle.textContent = "환불 요청";
            modalBody.innerHTML = `선택하신 결제건을<br><strong>정말 환불하시겠습니까?</strong>`;
            btnMain.textContent = "환불";
            btnMain.className = "btn-main"; // 혹시 이전 상태가 남아있을 수도 있으므로 초기화

            // ✅ 환불 요청 실행
            btnMain.onclick = function () {
                bsModal.hide();

                fetch("/payment/cancel", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                        [csrfHeader]: csrfToken
                    },
                    body: "paymentKey=" + paymentKey
                })
                    .then(res => res.json())
                    .then(data => {
                        modalTitle.textContent = "결과 안내";

                        if (data.status === "SUCCESS") {
                            modalBody.innerHTML = `<strong>환불이 완료되었습니다.</strong>`;
                            btnMain.textContent = "확인";
                            btnMain.onclick = () => location.reload();
                        } else {
                            modalBody.innerHTML = `<strong>환불 실패:</strong><br>${data.error || "알 수 없는 오류가 발생했습니다."}`;
                            btnMain.textContent = "닫기";
                            btnMain.onclick = () => bsModal.hide();
                        }

                        bsModal.show(); // ✅ 결과 모달 다시 띄우기
                    })
                    .catch(err => {
                        console.error("환불 요청 중 오류:", err);
                        modalTitle.textContent = "오류 발생";
                        modalBody.innerHTML = "요청 처리 중 오류가 발생했습니다.<br>잠시 후 다시 시도해주세요.";
                        btnMain.textContent = "닫기";
                        btnMain.onclick = () => bsModal.hide();
                        bsModal.show();
                    });
            };

            // 확인용 모달 표시
            bsModal.show();
        });
    });
});
