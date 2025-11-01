<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>LiVO</title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>

    <!-- CSRF -->
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <!-- 파비콘 (브라우저 탭 아이콘) -->
    <link rel="shortcut icon" href="<c:url value='/img/common/favicon.ico'/>" type="image/x-icon"/>
    <!-- Bootstrap -->
    <link  href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"/>
    <!-- Swiper -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css"/>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="<c:url value='/css/reset.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/css/common.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/css/lecture-play.css'/>"/>

    <!-- jQuery 관련 -->
    <%--      <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>--%>
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <!-- Swiper JS -->
    <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>
    <!-- Custom js -->
    <script src="/js/bgm-inline.js"></script>

</head>
<body>

<c:url var="loginUrl" value="/auth/login"/>
<c:url var="joinUrl" value="/auth/register"/>
<c:url var="logoutUrl" value="/auth/logout"/>


<section id="lecture" class="d-flex">
  <!-- 왼쪽: 비디오 영역 -->
    <div class="video-area flex-grow-1 p-3">
        <!-- YouTube 플레이어가 동적으로 여기에 렌더링됨 -->
        <div id="lectureVideo" data-lecture-id="${lecture.lectureId}"></div>
    </div>
<%--  <div class="video-area flex-grow-1 p-3">--%>
<%--    <c:if test="${not empty youtubeUrl}">--%>
<%--      <c:set var="url" value="${youtubeUrl}" />--%>
<%--      <c:choose>--%>
<%--        <c:when test="${fn:contains(url, 'watch?v=')}">--%>
<%--          <c:set var="embedUrl" value="${fn:replace(url, 'watch?v=', 'embed/')}" />--%>
<%--        </c:when>--%>
<%--        <c:when test="${fn:contains(url, 'youtu.be/')}">--%>
<%--          <c:set var="embedUrl" value="${fn:replace(url, 'youtu.be/', 'www.youtube.com/embed/')}" />--%>
<%--        </c:when>--%>
<%--        <c:when test="${fn:contains(url, 'shorts/')}">--%>
<%--          <c:set var="embedUrl" value="${fn:replace(url, 'shorts/', 'embed/')}" />--%>
<%--        </c:when>--%>
<%--        <c:otherwise>--%>
<%--          <c:set var="embedUrl" value="${url}" />--%>
<%--        </c:otherwise>--%>
<%--      </c:choose>--%>

<%--      <iframe id="lectureVideo"--%>
<%--              src="${embedUrl}"--%>
<%--              width="100%"--%>
<%--              height="500"--%>
<%--              title="YouTube video player"--%>
<%--              frameborder="0"--%>
<%--              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"--%>
<%--              allowfullscreen>--%>
<%--      </iframe>--%>
<%--    </c:if>--%>

<%--    <c:if test="${empty youtubeUrl}">--%>
<%--      <p class="text-center text-muted mt-5">등록된 영상이 없습니다.</p>--%>
<%--    </c:if>--%>
<%--  </div>--%>

  <!-- 오른쪽: 커리큘럼 -->
  <aside class="curriculum border-start p-4">
      <div class="lecture-close-button">
          <a href="/mypage/lecture" class="btn-point">닫기</a>
      </div>
    <h3 class="fw-bold mb-4">${lecture.title}</h3>
    <h6 class="text-secondary">${lecture.tutorName}</h6>

    <div class="progress-wrap mb-4">
      <div class="progress" style="height: 8px;">
        <div id="progressBar" class="bar bg-success" style="height: 8px;"></div>
      </div>
      <p class="progress-text mt-2 mb-0 text-secondary">
          진도율 <span id="progressText">0</span> %
      </p>
    </div>

    <!-- 아코디언 -->
    <div class="accordion" id="curriculumAccordion">
      <c:forEach var="chapter" items="${chapters}" varStatus="status">
        <div class="accordion-item">
          <h2 class="accordion-header" id="heading${status.index}">
            <button class="accordion-button collapsed" type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#collapse${status.index}"
                    aria-expanded="false"
                    aria-controls="collapse${status.index}">
              챕터 ${chapter.chapterOrder}. ${chapter.chapterName}
            </button>
          </h2>
          <div id="collapse${status.index}" class="accordion-collapse collapse"
               aria-labelledby="heading${status.index}"
               data-bs-parent="#curriculumAccordion">
            <div class="accordion-body">
              <p>${chapter.content}</p>
              <button class="btn-outline-main play-chapter mt-4"
                      data-url="${chapter.youtubeUrl}">
                ▶ 재생
              </button>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </aside>
</section>

<!-- JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://www.youtube.com/iframe_api"></script>
<script>
    // JSP의 chapters 데이터를 JS 배열로 변환
    <%--const chapters = [--%>
    <%--    <c:forEach var="ch" items="${chapters}" varStatus="st">--%>
    <%--    "${ch.chapterName}"<c:if test="${!st.last}">,</c:if>--%>
    <%--    </c:forEach>--%>
    <%--];--%>

    let player;
    let progressInterval;
    const lectureId = document.getElementById("lectureVideo").dataset.lectureId;
    const lastWatchedTime = <c:out value="${lastWatchedTime != null ? lastWatchedTime : 0}" default="0"/>;
    console.log("🎬 lastWatchedTime:", lastWatchedTime);

    // videoId와 startSeconds 파싱 함수
    function parseYouTubeUrl(url) {
        let videoId = "";
        let startSeconds = 0;
        if (!url) return { videoId, startSeconds };

        // 🎬 URL 형태별 videoId 추출
        if (url.includes("watch?v=")) {
            videoId = url.split("v=")[1]?.split("&")[0];
        } else if (url.includes("youtu.be/")) {
            videoId = url.split("youtu.be/")[1]?.split(/[?&]/)[0];
        } else if (url.includes("embed/")) {
            videoId = url.split("embed/")[1]?.split(/[?&]/)[0];
        } else if (url.includes("shorts/")) {
            videoId = url.split("shorts/")[1]?.split(/[?&]/)[0];
        }

        // 🎯 start 또는 t 파라미터 추출
        const startMatch = url.match(/[?&](start|t)=(\d+)/);
        if (startMatch) startSeconds = parseInt(startMatch[2]);

        return { videoId, startSeconds };
    }

    // 서버에서 넘어온 기본 강의(메인) 영상
    const youtubeUrl = '<c:out value="${youtubeUrl}" escapeXml="false"/>';
    const { videoId: initialVideoId } = parseYouTubeUrl(youtubeUrl);

    // YouTube API 로드 후 자동 실행
    function onYouTubeIframeAPIReady() {
        console.log("🔥 API Ready 실행됨");

        const startTime = lastWatchedTime && lastWatchedTime > 0 ? lastWatchedTime : 0;

        if (!initialVideoId) {
            console.error("❌ 초기 videoId 없음 (서버에서 youtubeUrl 확인 필요)");
            return;
        }


        player = new YT.Player("lectureVideo", {
            videoId: initialVideoId,
            width: "100%",
            height: "500",
            playerVars: {
                autoplay: 1,
                controls: 1,
                rel: 0,
                modestbranding: 1,
                start: startTime
            },
            events: {
                onReady: onPlayerReady,
                onStateChange: onPlayerStateChange
            }
        });
    }

    function onPlayerReady(event) {
        console.log("✅ onPlayerReady 실행됨");

        // ✅ 1. 비디오 준비된 직후, 지정한 시점으로 이동
        if (lastWatchedTime && lastWatchedTime > 0) {
            player.seekTo(lastWatchedTime, true);
        }

        // ✅ 2. 자동 재생 시작
        player.playVideo();

        // ✅ 3. 진행률 감시 시작
        startProgressLoop();
    }

    function onPlayerStateChange(event) {
        if (event.data === YT.PlayerState.ENDED) {
            document.getElementById("progressBar").style.width = "100%";

            const cur = player.getCurrentTime();
            const dur = player.getDuration();
            const percent = (cur / dur) * 100;

            saveProgress(lectureId, percent, cur);
        }
    }

    function startProgressLoop() {
        if (progressInterval) clearInterval(progressInterval);
        progressInterval = setInterval(() => {
            if (!player || !player.getDuration) return;
            const dur = player.getDuration();
            const cur = player.getCurrentTime();
            if (dur > 0) {
                //const percent = (cur / dur) * 100;
                // 진행률
                const percent = Math.floor((cur / dur) * 100);

                document.getElementById("progressBar").style.width = percent + "%";
                document.getElementById("progressText").innerText = percent;

                saveProgress(percent, cur); //lectureId는 전역변수니까 여기서 넘겨주지 않아도 값 있음
            }
        }, 1000);
    }

    // 현재 진행률 서버에 전송
    function saveProgress(percent, currentTime) {
        const csrfHeader = document.querySelector("meta[name='_csrf_header']").content;
        const csrfToken = document.querySelector("meta[name='_csrf']").content;

        fetch("/mypage/save", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({
                lectureId: lectureId,
                progressPercent: percent,
                lastWatchedTime: currentTime
            })
        })
            .then(res => {
                if(!res.ok) throw new Error("서버 통신 실패");
                return res.text();
            })
            .then(msg => console.log("✅ 진행률 저장 완료:", msg))
            .catch(err => console.error("❌ 진행률 저장 실패:", err));
    }


    // 챕터 버튼 클릭 시
    $(document).on("click", ".play-chapter", function() {
        const rawUrl = $(this).data("url");
        const { videoId, startSeconds } = parseYouTubeUrl(rawUrl);

        if (!player || !videoId) {
            return;
        }

        // 같은 영상이면 startSeconds로 이동, 아니면 새 영상 로드
        if (videoId === player.getVideoData().video_id) {
            player.seekTo(startSeconds || 0, true);
            player.playVideo();
        } else {
            player.loadVideoById({
                videoId: videoId,
                startSeconds: startSeconds || 0,
                suggestedQuality: "hd720"
            });
        }
    });
</script>

<script>
    <%--    let player;--%>
    <%--    let progressInterval;--%>

    <%--    // videoId와 startSeconds 추출 함수--%>
    <%--    function parseYouTubeUrl(url) {--%>
    <%--    let videoId = "";--%>
    <%--    let startSeconds = 0;--%>

    <%--    if (!url) return { videoId, startSeconds };--%>

    <%--    // 기본 videoId 추출--%>
    <%--    if (url.includes("watch?v=")) {--%>
    <%--    videoId = url.split("v=")[1]?.split("&")[0];--%>
    <%--} else if (url.includes("youtu.be/")) {--%>
    <%--    videoId = url.split("youtu.be/")[1]?.split(/[?&]/)[0];--%>
    <%--} else if (url.includes("embed/")) {--%>
    <%--    videoId = url.split("embed/")[1]?.split(/[?&]/)[0];--%>
    <%--} else if (url.includes("shorts/")) {--%>
    <%--    videoId = url.split("shorts/")[1]?.split(/[?&]/)[0];--%>
    <%--}--%>

    <%--    // start 파라미터 추출--%>
    <%--    const startMatch = url.match(/[?&]start=(\d+)/);--%>
    <%--    if (startMatch) startSeconds = parseInt(startMatch[1]);--%>

    <%--    return { videoId, startSeconds };--%>
    <%--}--%>

    <%--    // JSP에서 넘어온 초기 URL--%>
    <%--    const youtubeUrl = '<c:out value="${youtubeUrl}" escapeXml="false"/>';--%>
    <%--    const { videoId: initialVideoId, startSeconds: initialStart } = parseYouTubeUrl(youtubeUrl);--%>

    <%--    // YouTube API 로드 완료 시 호출--%>
    <%--    function onYouTubeIframeAPIReady() {--%>
    <%--    console.log("🔥 API Ready 실행됨");--%>

    <%--    player = new YT.Player("lectureVideo", {--%>
    <%--    videoId: initialVideoId,--%>
    <%--    width: "100%",--%>
    <%--    height: "500",--%>
    <%--    playerVars: {--%>
    <%--    autoplay: 0,--%>
    <%--    controls: 1,--%>
    <%--    rel: 0,--%>
    <%--    modestbranding: 1,--%>
    <%--    start: initialStart || 0--%>
    <%--},--%>
    <%--    events: {--%>
    <%--    onReady: onPlayerReady,--%>
    <%--    onStateChange: onPlayerStateChange--%>
    <%--}--%>
    <%--});--%>
    <%--}--%>

    <%--    // Player 준비 시--%>
    <%--    function onPlayerReady() {--%>
    <%--    console.log("✅ Player Ready");--%>
    <%--    startProgressLoop();--%>
    <%--}--%>

    <%--    // 상태 변화 감지--%>
    <%--    function onPlayerStateChange(event) {--%>
    <%--    if (event.data === YT.PlayerState.ENDED) {--%>
    <%--    document.getElementById("progressBar").style.width = "100%";--%>
    <%--}--%>
    <%--}--%>

    <%--    // 진행률 업데이트 루프--%>
    <%--    function startProgressLoop() {--%>
    <%--    if (progressInterval) clearInterval(progressInterval);--%>
    <%--    progressInterval = setInterval(() => {--%>
    <%--    if (!player || !player.getDuration) return;--%>
    <%--    const dur = player.getDuration();--%>
    <%--    const cur = player.getCurrentTime();--%>
    <%--    if (dur > 0) {--%>
    <%--    const percent = (cur / dur) * 100;--%>
    <%--    document.getElementById("progressBar").style.width = percent + "%";--%>
    <%--}--%>
    <%--}, 1000);--%>
    <%--}--%>

    <%--    // 챕터 버튼 클릭 시 새 영상 로드--%>
    <%--    $(document).on("click", ".play-chapter", function() {--%>
    <%--    const rawUrl = $(this).data("url");--%>
    <%--    const { videoId, startSeconds } = parseYouTubeUrl(rawUrl);--%>

    <%--    console.log("🎯 챕터 이동 요청:", videoId, "시작:", startSeconds);--%>

    <%--    if (!player || !videoId) {--%>
    <%--    console.error("❌ player 초기화 실패 또는 videoId 없음");--%>
    <%--    return;--%>
    <%--}--%>

    <%--    try {--%>
    <%--    player.loadVideoById({--%>
    <%--    videoId: videoId,--%>
    <%--    startSeconds: startSeconds || 0,--%>
    <%--    suggestedQuality: "hd720"--%>
    <%--});--%>
    <%--    console.log("✅ 영상 전환 성공:", videoId, "→", startSeconds, "초부터 재생");--%>
    <%--} catch (e) {--%>
    <%--    console.error("🚨 loadVideoById 실행 오류:", e);--%>
    <%--}--%>
    <%--});--%>


    <%--}--%>
    <%--  // 🎬 챕터 버튼 클릭 시 해당 영상으로 변경--%>
    //   $(document).on("click", ".play-chapter", function() {
    //     const rawUrl = $(this).data("url");
    //     let embedUrl = "";
    //
    //     if (rawUrl.includes("watch?v=")) {
    //       embedUrl = rawUrl.replace("watch?v=", "embed/");
    //     } else if (rawUrl.includes("youtu.be/")) {
    //       embedUrl = rawUrl.replace("youtu.be/", "www.youtube.com/embed/");
    //     } else if (rawUrl.includes("shorts/")) {
    //       embedUrl = rawUrl.replace("shorts/", "embed/");
    //     } else {
    //       embedUrl = rawUrl;
    //     }
    //
    //     $("#lectureVideo").attr("src", embedUrl);
    //   });


</script>
</body>
</html>