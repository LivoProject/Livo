const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
const form = document.getElementById("chatForm");
const input = document.getElementById("chatInput");
const chatBody = document.getElementById("chat-panel-body"); // 스크롤 영역

form.addEventListener("submit", function (e) {
    e.preventDefault();
    const question = input.value.trim();
    if (question === "") return;

    // 사용자 메시지 표시
    chatBody.innerHTML += `<div class="message user-message">${question}</div>`;
    chatBody.scrollTop = chatBody.scrollHeight;

    //  로딩 말풍선 추가
    const loading = document.createElement("div");
    loading.classList.add("loading-message");
    loading.innerHTML = `<span class="loading-dots"></span>`;
    chatBody.appendChild(loading);
    chatBody.scrollTop = chatBody.scrollHeight;

    // 서버 비동기 요청
    fetch("/faq/ask-ajax", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded",
            [csrfHeader]: csrfToken},
        body: "question=" + encodeURIComponent(question)
    })
        .then(res => res.json())
        .then(data => {
            loading.remove();
            chatBody.innerHTML += `<div class="message ai-message">${data.ai}</div>`;
            chatBody.scrollTop = chatBody.scrollHeight; // ✅ 자동 스크롤
        });

    input.value = "";
});
