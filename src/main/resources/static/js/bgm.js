(function () {
    // 메인 페이지에서만 실행(상대경로 기준 조정 가능)
    var path = window.location.pathname || "/";
    // 필요하면 특정 JSP만: if (!/^\/(?:|main)$/.test(path)) return;

    // 서버에서 내려준 플래그(footer.jsp에서 세팅)
    var allowedByServer = !!window.__BGM_ALLOWED__;
    if (!allowedByServer) return;

    var audioSrc = "/audio/login_success.mp3";  // 위치 그대로
    var storageKey = "ALLOW_BGM";

    // 로그인 클릭에서 남긴 흔적 여부(+ 새로고침 감지)
    var nav = (performance.getEntriesByType && performance.getEntriesByType("navigation")[0]) || {};
    var cameFromReload = (nav && nav.type === "reload");
    var allowByGestureFlag = false;
    try { allowByGestureFlag = (sessionStorage.getItem(storageKey) === "1"); } catch (e) {}

    // 오디오 엘리먼트 준비
    var audio = document.createElement("audio");
    audio.src = audioSrc;
    audio.loop = true;
    audio.preload = "auto";
    audio.crossOrigin = "anonymous"; // CORS 이슈 없으면 상관없음
    // iOS/Safari 호환용
    audio.setAttribute("playsinline", "");
    audio.setAttribute("webkit-playsinline", "");
    document.body.appendChild(audio);

    // 유틸: 오디오 재생 시도
    function tryPlay(unmute) {
        if (unmute) audio.muted = false; else audio.muted = true;
        var p = audio.play();
        if (p && typeof p.then === "function") {
            return p.then(function () { return true; }).catch(function () { return false; });
        }
        // 과거 브라우저
        return Promise.resolve(true);
    }

    // 1단계: 무음 자동재생 (대부분 허용)
    function startMutedAutoplay() {
        return tryPlay(false);
    }

    // 2단계: 조건되면 언뮤트
    function unmuteIfAllowed() {
        var shouldUnmute = allowByGestureFlag || cameFromReload;
        if (!shouldUnmute) return Promise.resolve(false);
        return tryPlay(true).then(function (ok) {
            // 한 번 성공했으면 깔끔하게 플래그 제거(선택)
            if (ok) { try { sessionStorage.removeItem(storageKey); } catch (e) {} }
            return ok;
        });
    }

    // 3단계: 사용자 제스처 “첫 번”에 확실히 언뮤트/재생
    var interacted = false;
    function onFirstUserInteraction() {
        if (interacted) return;
        interacted = true;
        // 바로 언뮤트 재생 시도
        tryPlay(true).then(function (ok) {
            if (ok) {
                try { sessionStorage.removeItem(storageKey); } catch (e) {}
                removeInteractionListeners();
            } else {
                // 혹시라도 실패하면 짧게 재시도
                setTimeout(function () {
                    tryPlay(true).then(function (ok2) {
                        if (ok2) {
                            try { sessionStorage.removeItem(storageKey); } catch (e) {}
                            removeInteractionListeners();
                        }
                    });
                }, 0);
            }
        });
    }

    function addInteractionListeners() {
        window.addEventListener("pointerdown", onFirstUserInteraction, { once: true, passive: true });
        window.addEventListener("keydown", onFirstUserInteraction, { once: true });
        window.addEventListener("touchstart", onFirstUserInteraction, { once: true, passive: true });
    }
    function removeInteractionListeners() {
        window.removeEventListener("pointerdown", onFirstUserInteraction);
        window.removeEventListener("keydown", onFirstUserInteraction);
        window.removeEventListener("touchstart", onFirstUserInteraction);
    }

    // 4단계: 탭 복귀/가시성 전환 시 자동 복구
    document.addEventListener("visibilitychange", function () {
        if (!document.hidden) {
            // 탭을 다시 볼 때 소리 계속 보장
            if (!audio.paused) {
                // 이미 재생 중이면 언뮤트만 확실히
                if (allowByGestureFlag) audio.muted = false;
            } else {
                // 정지돼 있으면 다시 시도
                if (allowByGestureFlag) tryPlay(true);
                else tryPlay(false);
            }
        }
    });

    // 5단계: bfcache 복귀(pageshow)에서도 복구
    window.addEventListener("pageshow", function (e) {
        if (e.persisted) {
            if (allowByGestureFlag) tryPlay(true);
            else tryPlay(false);
        }
    });

    // 실행 플로우
    startMutedAutoplay().then(function () {
        // 무음 재생은 거의 항상 OK
        unmuteIfAllowed().then(function (unmuted) {
            if (!unmuted) {
                // 아직 소리가 없다면 다음 사용자 제스처를 기다렸다가 풀자
                addInteractionListeners();
            }
        });
    }).catch(function () {
        // 무음 재생마저 거절되는 드문 환경 → 첫 상호작용에서 처리
        addInteractionListeners();
    });
})();
