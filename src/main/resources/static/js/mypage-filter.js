document.addEventListener("DOMContentLoaded", function () {
    const $searchBtn = $("#searchLectureBtn");
    const $keyword = $("#keyword");
    const $sort = $("#sortSelect");
    const container = document.getElementById("lectureContainer");

    function fetchLectures(keyword = "", sort = "latest") {
        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        $.ajax({
            url: "/mypage/lecture/search",
            type: "POST",
            data: { keyword, sort },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (res) {
                if (!res || res.success === false) {
                    container.innerHTML = "<p class='text-muted'>검색 결과가 없습니다.</p>";
                    return;
                }

                const list = res.data || [];
                if (!list.length) {
                    container.innerHTML = "<p class='text-muted'>검색 결과가 없습니다.</p>";
                    return;
                }

                let html = "";
                list.forEach((r) => {
                    const isSaleClosed = r.visibility === "DELETED";
                    const isLectureFinished = r.lectureStatus === "ENDED";

                    const badgeHTML = isSaleClosed
                        ? `<span class="badge bg-secondary ms-1">판매 종료</span>`
                        : "";

                    const playButtonDisabled = isLectureFinished
                        ? 'style="pointer-events:none;opacity:0.5;"'
                        : "";

                    const buttonHTML = isLectureFinished
                        ? `<button class="btn btn-sm btn-secondary" disabled>수강 종료됨</button>`
                        : `<button class="btn-unreserve btn-main"
                                data-lecture-id="${r.lectureId}"
                                data-bs-toggle="modal"
                                data-bs-target="#reserveModal">예약 취소</button>`;

                    const viewLinkStart = isLectureFinished
                        ? `<a href="javascript:void(0);" onclick="alert('수강 기간이 종료된 강의입니다. 다시 수강을 원하시면 재결제 후 이용해주세요.'); return false;">`
                        : `<a href="/lecture/view/${r.lectureId}">`;

                    html += `
                        <div class="card">
                            <div class="card-img-wrap">
                                ${viewLinkStart}
                                    <img src="${r.thumbnailUrl}" class="card-img-top" alt="${r.title}">
                                    <button class="play-btn" ${playButtonDisabled}>
                                        <i class="bi bi-play-fill"></i>
                                    </button>
                                </a>
                            </div>

                            <div class="card-body">
                                <a href="/lecture/content/${r.lectureId}">
                                    <h6 class="fw-bold text-ellipsis-2 lecture-title">${r.title} ${badgeHTML}</h6>
                                    <p class="text-muted">${r.tutorName}</p>
                                    <div class="progress" style="height:8px;">
                                        <div class="progress-bar bg-success" style="width:${r.progressPercent}%;"></div>
                                    </div>
                                    <small class="text-muted">${r.progressPercent}%</small>
                                </a>
                            </div>

                            <div class="card-footer">
                                <div class="button-wrap">
                                    ${buttonHTML}
                                    <a href="/lecture/content/${r.lectureId}#review" class="btn-cancel">
                                        수강평 작성
                                    </a>
                                </div>
                            </div>
                        </div>`;
                });

                container.innerHTML = html;
            },
            error: function (xhr) {
                console.error(xhr);
                alert("검색 중 오류가 발생했습니다.");
            },
        });
    }


    $searchBtn.on("click", function (e) {
        e.preventDefault();
        const keyword = $keyword.val().trim();
        const sort = $sort.val();
        if (!keyword) return alert("검색어를 입력하세요.");
        fetchLectures(keyword, sort);
    });

    $keyword.on("keypress", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            const keyword = $keyword.val().trim();
            const sort = $sort.val();
            if (!keyword) return alert("검색어를 입력하세요.");
            fetchLectures(keyword, sort);
        }
    });

    $sort.on("change", function () {
        const keyword = $keyword.val().trim();
        fetchLectures(keyword, $(this).val());
    });
});
