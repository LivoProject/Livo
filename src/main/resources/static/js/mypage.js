document.addEventListener("DOMContentLoaded", function () {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 좋아요 해제
    document.querySelectorAll(".btn-unlike").forEach(button => {
        button.addEventListener("click", function () {
            const lectureId = this.dataset.lectureId;

            if (confirm("좋아요를 해제하시겠습니까?")) {
                fetch("/mypage/like/delete", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
                        [csrfHeader]: csrfToken
                    },
                    body: new URLSearchParams({lectureId: lectureId}).toString()
                })
                    .then(res => {
                        if (!res.ok) throw new Error(res.statusText);
                        return res.json();
                    })
                    .then(data => {
                        if (data.success) {
                            alert("좋아요가 해제되었습니다.");
                            this.closest(".card").remove();
                        } else {
                            alert("실패: " + data.message);
                        }
                    })
                    .catch(err => alert("실패: " + err.message));
            }
        });
    });
});