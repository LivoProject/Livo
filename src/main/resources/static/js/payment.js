const _csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
const _csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
// Toss Payments 위젯 초기화
const clientKey = "test_ck_KNbdOvk5rkWqJ4doGJWqrn07xlzm";
const customerKey = "user_" + new Date().getTime();
const tossPayments  = TossPayments(clientKey);
//회원결제
const payment = tossPayments.payment({customerKey});
//현재 로그인 유저의 이메일
async function requestPayment() {
    try {
        //결제 전 예약 생성
        const response = await fetch(`/reservation/create/${lectureId}`,{
            method: "POST",
            headers: {
                "Content-Type":"application/json",
                [_csrfHeader]: _csrfToken
            }
        });

        const data = await response.json();
        if (!data.success) {
            alert(data.message || "예약 생성 실패");
            return;
        }
        const reservationId = data.reservationId;
        console.log("예약 생성됨, ID:"+reservationId);
        //결제요청
        await payment.requestPayment({
            method: "CARD", // 카드 결제
            amount: {
                currency: "KRW",
                value: amount, // 결제 금액
            },
            orderId: "ORD-" + new Date().getTime(), // 주문번호
            orderName: lectureName,
            successUrl: window.location.origin + `/payment/confirm?reservationId=${reservationId}&lectureId=${lectureId}`,
            failUrl: window.location.origin + `/payment/fail`,
            customerEmail: "test@naver.com",
            customerName: "홍길동",
            customerMobilePhone: "01012341234",
            card: {
                useEscrow: false,
                flowMode: "DEFAULT",
                useCardPoint: false,
                useAppCardOnly: false,
            },
        });
    } catch (error) {
        console.error("결제 요청 중 오류 발생:", error);
        alert("결제 요청 실패: " + error.message);
    }
}
