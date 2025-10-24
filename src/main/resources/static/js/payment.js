const _csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
const _csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");
// Toss Payments 위젯 초기화
const clientKey = "test_ck_KNbdOvk5rkWqJ4doGJWqrn07xlzm";
const customerKey = "user_" + new Date().getTime();
const tossPayments  = TossPayments(clientKey);
//회원결제
const payment = tossPayments.payment({customerKey});
// 결제수단 선택 영역 렌더링
// paymentWidget.renderPaymentMethods("#payment-method", { value: 1000 });
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
                value: 1000, // 결제 금액
            },
            orderId: "ORD-" + new Date().getTime(), // 주문번호
            orderName: "LIVO 강좌 결제",
            successUrl:
                window.location.origin +
                "/payment/success?" +
                "&reservationId=" + reservationId +
                "&lectureId=" + lectureId+
                "&amount=" + 1000,
            failUrl: window.location.origin + "/payment/fail",
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
// document.getElementById("payButton").addEventListener("click", async () => {
//     await payment.requestPayment({
//         method: "CARD", // 카드 결제
//         amount: {
//             currency: "KRW",
//             value: 50000,
//         },
//         orderId: "ORD-" + new Date().getTime(),
//         orderName: "LIVO 강좌 결제",
//         amount: 1000,
//         successUrl: window.location.origin + "/success", // 결제 요청이 성공하면 리다이렉트되는 URL
//         failUrl: window.location.origin + "/fail", // 결제 요청이 실패하면 리다이렉트되는 URL
//         // customerEmail: "customer123@gmail.com",
//         // customerName: "김토스",
//         // customerMobilePhone: "01012341234",
//         // 카드 결제에 필요한 정보
//         card: {
//             useEscrow: false,
//             flowMode: "DEFAULT", // 통합결제창 여는 옵션
//             useCardPoint: false,
//             useAppCardOnly: false,
//         },
//     });
//     // paymentWidget.requestPayment({
//     //     orderId: "ORD-" + new Date().getTime(),
//     //     orderName: "LIVO 강좌 결제",
//     //     amount: 1000,
//     //     successUrl: "http://localhost:8080/payment/success",
//     //     failUrl: "http://localhost:8080/payment/fail"
//     // });
// });

// ✅ (테스트용) 직접 confirm 요청
async function testConfirm() {
    const data = {
        orderId: "ORD-20251026-001",
        paymentKey: "tviva20251024113535SXwM8", //결제성공시 나온거
        amount: 1000,
        email: "test@naver.com",
        lectureId: 1,
        reservationId: 5,
    };

    const res = await fetch("/payment/confirm", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [_csrfHeader]: _csrfToken, // ✅ 여기에 직접 붙이기
        },
        body: JSON.stringify(data),
    });

    const result = await res.json();
    console.log(result);
}
