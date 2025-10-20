// const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
// const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

// $(document).ajaxSend(function(e, xhr, options) {
//     xhr.setRequestHeader(csrfHeader, csrfToken);
// });
$(document).ready(function() {
    const lectureId = window.location.pathname.split("/").pop();

    $.ajax({
        url: "/admin/chapter/list/" + lectureId,
        type: "GET",
        success: function(chapters) {
            if (!chapters || chapters.length === 0) {
                console.log("챕터 없음");
                return;
            }

            const accordion = $("#curriculumAccordion");
            accordion.empty(); // 기존 내용 지움

            chapters.forEach((c, idx) => {
                const item = `
          <div class="accordion-item">
            <h2 class="accordion-header" id="heading${idx}">
              <button class="accordion-button ${idx === 0 ? "" : "collapsed"}"
                      type="button"
                      data-bs-toggle="collapse"
                      data-bs-target="#collapse${idx}"
                      aria-expanded="${idx === 0}"
                      aria-controls="collapse${idx}">
                ${idx + 1}. ${c.chapterName}
              </button>
            </h2>
            <div id="collapse${idx}" class="accordion-collapse collapse ${idx === 0 ? "show" : ""}"
                 aria-labelledby="heading${idx}" data-bs-parent="#curriculumAccordion">
              <div class="accordion-body">
                <a href="#" class="chapter-link" data-url="${c.youtubeUrl}">
                  ${c.chapterName}
                </a>
              </div>
            </div>
          </div>`;
                accordion.append(item);
            });

            // 첫 번째 영상 자동 로드
            $("#lectureVideo").attr("src", chapters[0].youtubeUrl);
        },
        error: function(xhr) {
            console.error("챕터 목록 불러오기 실패:", xhr);
        }
    });

    // 클릭 시 영상 교체
    $(document).on("click", ".chapter-link", function(e) {
        e.preventDefault();
        const url = $(this).data("url");
        $("#lectureVideo").attr("src", url);
    });
});
