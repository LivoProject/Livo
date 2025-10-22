const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(csrfHeader, csrfToken);
});
console.log("categoryId:", sessionStorage.getItem("categoryId"));
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
// 챕터 데이터 수집
function collectChapterData() {
    const chapters = [];
    $(".chapter").each(function () {
        chapters.push({
            chapterName: $(this).find(".chapterName").val(),
            chapterOrder: $(this).find(".chapterOrder").val(),
            youtubeUrl: $(this).find(".youtubeUrl").val(),
            content: $(this).find(".chapterContent").val()
        });
    });
    return chapters;
}
// 강의 + 챕터 통합 저장
$('#submitAllBtn').click(function () {
    const lecture = JSON.parse(sessionStorage.getItem('tempLecture'));
    const categoryId = sessionStorage.getItem('categoryId');
    if (!lecture) {
        alert('이전 단계에서 강의 정보가 없습니다. 다시 등록해주세요.');
        location.href = '/admin/lecture/insert';
        return;
    }
    if(!categoryId){
        alert('카테고리 정보가 누락되었습니다.');
        location.href ='admin/lecture/insert';
        return;
    }

    const chapters = collectChapterData();
    if (chapters.length === 0) {
        alert('최소 1개 이상의 챕터를 등록해야 합니다.');
        return;
    }

    const formData = new FormData();
    formData.append('lecture', new Blob([JSON.stringify(lecture)], { type: 'application/json' }));
    formData.append('chapters', new Blob([JSON.stringify(chapters)], { type: 'application/json' }));
    formData.append('categoryId', categoryId);

    $.ajax({
        url: '/admin/lecture/save',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function () {
            alert('강의와 챕터 등록이 완료되었습니다!');
            sessionStorage.removeItem('tempLecture');
            location.href = '/admin/lecture';
        },
        error: function (xhr) {
            console.error(xhr);
            alert('등록 중 오류가 발생했습니다.');
        }
    });
});
// // 수정용
// $("#editChapterBtn").click(function() {
//     const chapterId = $("#chapterId").val();
//     const lectureId = $("#lectureId").val();
//     const chapterName = $(".chapterName").val();
//     const youtubeUrl = $(".youtubeUrl").val();
//     const content = $(".content").val();
//
//     $.ajax({
//         url: "/admin/chapter/edit",
//         type: "PUT",
//         contentType: "application/json",
//         data: JSON.stringify({
//             chapterId,
//             lectureId,
//             chapterName,
//             youtubeUrl,
//             content
//         }),
//         success: function() {
//             alert("수정 완료!");
//             window.location.href = "/admin/chapter/list/" + lectureId;
//         },
//         error: function() {
//             alert("수정 중 오류가 발생했습니다.");
//         }
//     });
// });
