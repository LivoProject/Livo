const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(csrfHeader, csrfToken);
});
$(document).on('click', '.deleteBtn', function() {
    const type = $(this).data('type');
    const id = $(this).data('id');
    console.log("data-type:" +type);
    console.log("data-id" + id);
    if (!confirm('정말 삭제하시겠습니까?')) return;

    $.ajax({
        url: `/admin/${type}/delete/${id}`,
        type: 'POST',
        success: function() {
            alert('삭제 완료');
            location.reload();
        },
        error: function(err) {
            console.error(err);
            alert('삭제 실패');
        }
    });
});
$(document).on('click', '.editBtn', function() {
    const type = $(this).data('type');
    const id = $(this).data('id');

    let url = '';
    switch (type) {
        case 'lecture':
            url = `/admin/lecture/edit?lectureId=${id}`;
            break;
        case 'notice':
            url = `/admin/notice/edit?noticeId=${id}`;
            break;
        case 'faq':
            url = `/admin/faq/edit?faqId=${id}`;
            break;
        default:
            console.error('Unknown type: ',type)
            return;
    }
    window.location.href = url;
});

$(document).on('click', '.update-status', function () {
    const id = $(this).data('id');
    const action = $(this).data('action'); // approve / reject

    if (!confirm(`정말 ${action === 'approve' ? '승인' : '반려'} 처리하시겠습니까?`)) return;

    $.ajax({
        url: `/admin/report/${action}/${id}`,
        type: 'POST',
        success: function () {
            alert(`${action === 'approve' ? '승인' : '반려'} 완료되었습니다.`);
            location.reload();
        },
        error: function (err) {
            console.error(err);
            alert(`${action === 'approve' ? '승인' : '반려'} 처리 실패`);
        }
    });
});

$(document).on('click', '.toggle-notice-status', function () {
    const id = $(this).data('id');
    const action = $(this).data('action'); // visible / pin

    $.ajax({
        url: `/admin/notice/${action}/${id}`,
        type: 'POST',
        success: function () {
            location.reload();
        },
        error: function (err) {
            console.error(err);
            alert('처리 실패');
        }
    });
});
