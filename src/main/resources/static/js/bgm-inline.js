document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("bgmToggle");
    if (!btn) return; // 버튼이 없는 페이지면 종료

    const audio = new Audio("/audio/login_success.mp3");
    audio.loop = true;
    audio.volume = 0.6; // 볼륨 (0.0 ~ 1.0)

    // 저장된 상태 불러오기 (기본값: 켜짐)
    const saved = localStorage.getItem("BGM_ON");
    let isOn = saved === null ? true : saved === "true";

    // 초기 UI 세팅
    updateUi(isOn);

    // 자동 재생 시도
    if (isOn) {
        playAudio();
    }

    // 버튼 클릭 시 토글
    btn.addEventListener("click", () => {
        isOn = !isOn;
        localStorage.setItem("BGM_ON", isOn);
        updateUi(isOn);
        if (isOn) {
            playAudio();
        } else {
            audio.pause();
        }
    });

    // 오디오 재생 함수
    function playAudio() {
        audio.play().catch(err => {
            console.warn("브금 자동 재생이 차단됨:", err);
            // 사용자 클릭 시 재시도 가능
        });
    }

    // UI 상태 업데이트
    function updateUi(on) {
        btn.classList.toggle("bgm-on", on);
        btn.classList.toggle("bgm-off", !on);
        const label = btn.querySelector(".bgm-label");
        label.textContent = on ? "🔊" : "🔇";
        btn.setAttribute("aria-pressed", String(on));
        btn.title = on ? "배경음악 끄기" : "배경음악 켜기";
    }
});
