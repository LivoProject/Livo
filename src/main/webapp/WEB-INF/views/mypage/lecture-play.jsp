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
    <link rel="shortcut icon" href="/img/common/favicon.ico" type="image/x-icon"/>
    <!-- Bootstrap -->
    <link  href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"/>
    <!-- Swiper -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css"/>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="/css/reset.css"/>
    <link rel="stylesheet" href="/css/common.css"/>
    <link rel="stylesheet" href="/css/lecture-play.css"/>

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
        <div id="lectureVideo"></div>
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
    <h3 class="fw-bold mb-1">${lecture.title}</h3>
    <h6 class="text-secondary mb-4">${lecture.tutorName}</h6>

    <div class="progress-wrap mb-4">
      <div class="progress" style="height: 8px;">
        <div id="progressBar" class="bar bg-success" style="height: 8px;"></div>
      </div>
      <p class="progress-text mt-2 mb-0 text-secondary">
        진도율 <span>81</span> / 98 <span>(83%)</span>
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
              <p class="mb-2">${chapter.content}</p>
              <button class="btn btn-sm btn-outline-primary play-chapter"
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
    console.log("🔥 JSP 로드 테스트");

    // ✅ 서버에서 넘어온 URL 확인
    const youtubeUrl = '<c:out value="${youtubeUrl}" escapeXml="false"/>';
    console.log("🎥 youtubeUrl:", youtubeUrl);

    let videoId = "";

    // ✅ 유튜브 URL에서 videoId 추출
    if (youtubeUrl.includes("embed/")) {
        videoId = youtubeUrl.split("embed/")[1]?.split(/[?&]/)[0];
    } else if (youtubeUrl.includes("watch?v=")) {
        videoId = youtubeUrl.split("v=")[1]?.split("&")[0];
    } else if (youtubeUrl.includes("youtu.be/")) {
        videoId = youtubeUrl.split("youtu.be/")[1]?.split(/[?&]/)[0];
    } else if (youtubeUrl.includes("shorts/")) {
        videoId = youtubeUrl.split("shorts/")[1]?.split(/[?&]/)[0];
    }
    console.log("🎬 추출된 videoId:", videoId);

    let player;

    // ✅ YouTube API가 로드되면 자동 실행
    window.onYouTubeIframeAPIReady = function () {
        console.log("🔥 API Ready 실행됨");

        if (!videoId) {
            console.error("❌ videoId가 비어있습니다. 유튜브 URL 확인 필요!");
            return;
        }

        // ✅ player 생성
        player = new YT.Player("lectureVideo", {
            videoId: videoId,
            width: "100%",
            height: "500",
            playerVars: {
                autoplay: 0,
                controls: 1,
                rel: 0,
                modestbranding: 1,
            },
            events: {
                onReady: onPlayerReady,
                onStateChange: onPlayerStateChange,
            },
        });
        console.log("🎮 YT.Player 생성 완료");
    };

    // ✅ 준비 완료 시
    function onPlayerReady(event) {
        console.log("✅ onPlayerReady 실행됨");
        event.target.playVideo(); // 테스트용 자동재생

        // 5초마다 진행률 표시
        setInterval(updateProgressBar, 1000);
    }

    // ✅ 상태 변경 시
    function onPlayerStateChange(event) {
        if (event.data === YT.PlayerState.PLAYING) {
            console.log("▶ 영상 재생 중");
        } else if (event.data === YT.PlayerState.ENDED) {
            console.log("⏹ 영상 종료됨");
            document.getElementById("progressBar").style.width = "100%";
        }
    }

    // ✅ 프로그레스바 업데이트
    function updateProgressBar() {
        if (!player || !player.getDuration) return;

        const duration = player.getDuration();
        const current = player.getCurrentTime();

        if (duration > 0) {
            const percent = (current / duration) * 100;
            document.getElementById("progressBar").style.width = percent + "%";
            console.log(`📊 진행률: ${percent.toFixed(1)}%`);
        }
    }
    <%--  // 🎬 챕터 버튼 클릭 시 해당 영상으로 변경--%>
      $(document).on("click", ".play-chapter", function() {
        const rawUrl = $(this).data("url");
        let embedUrl = "";

        if (rawUrl.includes("watch?v=")) {
          embedUrl = rawUrl.replace("watch?v=", "embed/");
        } else if (rawUrl.includes("youtu.be/")) {
          embedUrl = rawUrl.replace("youtu.be/", "www.youtube.com/embed/");
        } else if (rawUrl.includes("shorts/")) {
          embedUrl = rawUrl.replace("shorts/", "embed/");
        } else {
          embedUrl = rawUrl;
        }

        $("#lectureVideo").attr("src", embedUrl);
          updateProgressBar();
      });

</script>
</body>
</html>