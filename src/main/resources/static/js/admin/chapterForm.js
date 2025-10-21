const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(csrfHeader, csrfToken);
});

let chapterIndex = 1;

$("#chapterContainer").sortable({
    placeholder: "chapter.css-placeholder",
    update: function(event, ui) {
        updateChapterOrder();
    }
});

$('#addChapterBtn').click(function() {
    chapterIndex++;
    const html = `
      <div class="chapter border rounded p-3 mb-3">
        <div class="d-flex justify-content-between align-items-center mb-3">
          <strong>Chapter <span class="chapter-index">${chapterIndex}</span></strong>
          <span class="text-muted small">(드래그하여 순서 변경 가능)</span>
        </div>

        <label>챕터명</label>
        <input type="text" class="form-control mt-2 mb-2 chapterName" />

        <input type="hidden" class="chapterOrder" value="${chapterIndex}" />

        <label>유튜브 URL</label>
        <input type="text" class="form-control mt-2 mb-2 youtubeUrl" />

        <label>내용</label>
        <textarea class="form-control content mt-2" rows="3"></textarea>
      </div>`;
    $('#chapterContainer').append(html);
    updateChapterOrder();
});

function updateChapterOrder() {
    $(".chapter.css").each(function(index) {
        const order = index + 1;
        $(this).find(".chapterOrder").val(order);
        $(this).find(".chapter.css-index").text(order);
    });
}

$('#saveChapterBtn').click(function() {
    const lectureId = $('#lectureId').val();
    const chapters = [];

    $('.chapter').each(function() {
        chapters.push({
            lectureId: lectureId ,
            chapterName: $(this).find('.chapterName').val(),
            chapterOrder: $(this).find('.chapterOrder').val(),
            youtubeUrl: $(this).find('.youtubeUrl').val(),
            content: $(this).find('.content').val()
        });
    });

    $.ajax({
        url: '/admin/chapter/save',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(chapters),
        success: function() {
            alert('챕터 등록이 완료되었습니다!');
            window.location.href = '/admin/lecture';
        },
        error: function() {
            alert('챕터 저장 중 오류가 발생했습니다.');
        }
    });
});
