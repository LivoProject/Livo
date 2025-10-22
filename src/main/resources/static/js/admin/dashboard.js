const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

$(document).ajaxSend(function(e, xhr, options) {
    xhr.setRequestHeader(csrfHeader, csrfToken);
});
document.addEventListener("DOMContentLoaded", function() {
    // 차트1
    const ctx1 = document.getElementById("chart1").getContext("2d");
    new Chart(ctx1, {
        type: "bar",
        data: {
            labels: ["A", "B", "C", "D"],
            datasets: [{
                label: "통계1",
                data: [10, 20, 30, 40],
                backgroundColor: "rgba(54, 162, 235, 0.5)"
            }]
        }
    });

    // 차트2
    const ctx2 = document.getElementById("chart2").getContext("2d");
    new Chart(ctx2, {
        type: "line",
        data: {
            labels: ["A", "B", "C", "D"],
            datasets: [{
                label: "통계2",
                data: [15, 25, 35, 45],
                borderColor: "rgba(255, 99, 132, 1)",
                fill: false
            }]
        }
    });

    // 차트3
    const ctx3 = document.getElementById("chart3").getContext("2d");
    new Chart(ctx3, {
        type: "pie",
        data: {
            labels: ["A", "B", "C"],
            datasets: [{
                label: "통계3",
                data: [30, 50, 20],
                backgroundColor: [
                    "rgba(255, 99, 132, 0.5)",
                    "rgba(54, 162, 235, 0.5)",
                    "rgba(255, 206, 86, 0.5)"
                ]
            }]
        }
    });
});
$(document).on('click', '#deleteBtn', function() {
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
$(document).on('click', '#editBtn', function() {
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
