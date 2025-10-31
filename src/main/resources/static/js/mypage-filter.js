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
                    const status = (r.lectureStatus || "").toUpperCase(); // ACTIVE / CLOSED / ENDED
                    const visibility = (r.visibility || "").toUpperCase(); // ACTIVE / DELETED

                    const isEnded = status === "ENDED"; // 수강마감
                    const isClosed = status === "CLOSED"; //  예약 마감
                    const isDeleted = visibility === "DELETED"; // 판매 종료

                    const start = new Date(r.lectureStart);
                    const end = new Date(r.lectureEnd);
                    const now = new Date();
                    const isBeforeStart = start > now;
                    const isInProgress = start <= now && now <= end;
                    const isLectureFinished = now > end;

                    const badgeHTML = isDeleted
                        ? `<span class="badge bg-secondary ms-1">판매 종료</span>`
                        : (isBeforeStart ? `<span class="badge bg-warning ms-1">수강 대기</span>` : "");

                    let thumbStyle = "";
                    let iconHTML = "";
                    let playButtonStyle = "";
                    let viewLink = `/lecture/view/${r.lectureId}`;
                    if(isEnded){
                        thumbStyle = `style="opacity:0.6; filter:grayscale(40%);"`;
                        iconHTML = `<i class="bi bi-stop-fill"></i>`;
                        playButtonStyle = `style="pointer-events:none; opacity:0.5;"`;
                        viewLink = `javascript:void(0);" onclick="alert('수강 기간이 종료된 강의입니다.'); return false;`;
                    }else if(isBeforeStart) {
                        thumbStyle = `style="opacity:0.5;"`;
                        iconHTML = `<i class="bi bi-play-fill"></i>`;
                        playButtonStyle = `style="pointer-events:none; opacity:0.5;"`;
                        viewLink = `javascript:void(0);" onclick="alert('수강 시작일 이후부터 시청할 수 있습니다.'); return false;`;
                    }else{
                        thumbStyle = ``;
                        iconHTML = `<i class="bi bi-play-fill"></i>`;
                        playButtonStyle = ``;
                        viewLink = `/lecture/view/${r.lectureId}`;
                    }

                    let buttonHTML = "";
                    if (isEnded) {
                        buttonHTML = `<button class="btn-secondary" disabled style="background: #8a959f; color: #121212">강의 종료</button>`;
                    } else if (isInProgress) {
                        buttonHTML = `<button class="btn btn-sm btn-outline-secondary" disabled>예약 취소 불가</button>`;
                    } else if(isDeleted){
                        buttonHTML = `<button class="btn btn-sm btn-outline-secondary" disabled>예약 취소 불가</button>`;
                    } else {
                        buttonHTML = `
                            <button class="btn-unreserve btn-main"
                                data-lecture-id="${r.lectureId}"
                                data-price="${r.price}"
                                data-bs-toggle="modal"
                                data-bs-target="#reserveModal">
                                예약 취소
                            </button>`;
                    }

                    html += `
                        <div class="card">
                            <div class="card-img-wrap">
                                <a href="${viewLink}">
                                  <img src="${r.thumbnailUrl}" class="card-img-top" ${thumbStyle} alt="${r.title}">
                                  <button class="play-btn" ${playButtonStyle}>
                                      ${iconHTML}
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
