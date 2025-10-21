<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section id="lecture" class="d-flex">
  <!-- 왼쪽: 비디오 영역 -->
  <div class="video-area flex-grow-1 p-3">
    <c:if test="${not empty youtubeUrl}">
      <c:set var="url" value="${youtubeUrl}" />
      <c:choose>
        <c:when test="${fn:contains(url, 'watch?v=')}">
          <c:set var="embedUrl" value="${fn:replace(url, 'watch?v=', 'embed/')}" />
        </c:when>
        <c:when test="${fn:contains(url, 'youtu.be/')}">
          <c:set var="embedUrl" value="${fn:replace(url, 'youtu.be/', 'www.youtube.com/embed/')}" />
        </c:when>
        <c:when test="${fn:contains(url, 'shorts/')}">
          <c:set var="embedUrl" value="${fn:replace(url, 'shorts/', 'embed/')}" />
        </c:when>
        <c:otherwise>
          <c:set var="embedUrl" value="${url}" />
        </c:otherwise>
      </c:choose>

  <link rel="stylesheet" href="/css/lecture-play.css">
  <link rel="stylesheet" href="/css/mypage.css">

  <section id="lecture">
    <!-- 왼쪽: 비디오 영역 -->
    <div class="video-area">
      <iframe src="https://www.youtube.com/embed/B-14Ksjonvk?si=JR4rw8rxElFyr3Vr" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
      <iframe id="lectureVideo"
              src="${embedUrl}"
              width="100%"
              height="500"
              title="YouTube video player"
              frameborder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen>
      </iframe>
    </c:if>

    <c:if test="${empty youtubeUrl}">
      <p class="text-center text-muted mt-5">등록된 영상이 없습니다.</p>
    </c:if>
  </div>

  <!-- 오른쪽: 커리큘럼 -->
  <aside class="curriculum border-start p-4" style="width: 400px;">
    <h3 class="fw-bold mb-1">${lecture.title}</h3>
    <h6 class="text-secondary mb-4">${lecture.tutorName}</h6>

    <div class="progress-wrap mb-4">
      <div class="progress" style="height: 8px;">
        <div class="bar bg-success" style="width: 83%; height: 8px;"></div>
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
<script>
  // 🎬 챕터 버튼 클릭 시 해당 영상으로 변경
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
  });
</script>
</body>
</html>
