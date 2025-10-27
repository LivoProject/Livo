function cancelPayment(paymentKey) {
    if (!confirm("정말 환불하시겠어요?")) return;

    fetch("/payment/cancel", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: "paymentKey=" + paymentKey
    })
        .then(res => res.json())
        .then(data => {
            if (data.status === "SUCCESS") {
                alert("환불 완료되었습니다.");
                location.reload();
            } else {
                alert("환불 실패: " + data.error);
            }
        });
}
