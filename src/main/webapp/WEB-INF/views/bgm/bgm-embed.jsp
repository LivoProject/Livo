<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>BGM Embed</title>
</head>
<body style="margin:0">
<!-- 실제 재생되는 오디오 -->
<audio id="bgm" src="<c:url value='/audio/login_success.mp3'/>"
       loop preload="auto"></audio>

<script>
    (function () {
        const audio = document.getElementById('bgm');

        // 부모에게 준비되었음을 알림
        window.parent && window.parent.postMessage({type:'bgm:ready'}, '*');

        // 부모 → iframe 제어
        window.addEventListener('message', async (e) => {
            const msg = e.data || {};
            if (msg.type !== 'bgm:ctrl') return;

            try {
                switch (msg.cmd) {
                    case 'play':
                        await audio.play();
                        window.parent.postMessage({type:'bgm:state', playing: !audio.paused}, '*');
                        break;
                    case 'pause':
                        audio.pause();
                        window.parent.postMessage({type:'bgm:state', playing: !audio.paused}, '*');
                        break;
                    case 'volume':
                        audio.volume = Math.max(0, Math.min(1, Number(msg.value ?? 1)));
                        break;
                    case 'seek':
                        if (typeof msg.value === 'number') audio.currentTime = msg.value;
                        break;
                }
            } catch (err) {
                // 자동재생 차단 등 실패 알림
                window.parent.postMessage({type:'bgm:error', message: String(err)}, '*');
            }
        });

        // 재생 상태 변경시 부모에 브로드캐스트 (선택)
        audio.addEventListener('play',  () => window.parent.postMessage({type:'bgm:state', playing:true},  '*'));
        audio.addEventListener('pause', () => window.parent.postMessage({type:'bgm:state', playing:false}, '*'));
    })();
</script>
</body>
</html>
