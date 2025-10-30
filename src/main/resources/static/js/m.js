
    $("#searchLectureBtn").on("click", function (e) {
    e.preventDefault();

    const keyword = $("#keyword").val().trim();
    if (!keyword) return alert("검색어를 입력하세요.");

    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    const container = document.getElementById("lectureContainer");

    $.ajax({
    url: "/mypage/lecture/search",
    type: "POST",
    data: {keyword},
    beforeSend: function (xhr) {
    xhr.setRequestHeader(header, token);
},
    success: function (res) {
    const list = res.data || [];
    window.lastSearchList = list;
    if (!list.length) {
    container.innerHTML = "<p class='text-muted'>검색 결과가 없습니다.</p>";
    return;
}

    let html = "";
    list.forEach((r) => {
    const isSaleClosed = r.visibility === 'DELETED'; //판매종료
    const isLectureFinished = r.status === 'ENDED'; //수강기간종료
    const isBeforeStart = new Date(r.lectureStart) > new Date(); // 수강기간전
    const isInProgress = new Date(r.lectureStart) <= new Date() && new Date() <= new Date(r.lectureEnd); // 수강중
    //뱃지
    const badgeHTML = isSaleClosed
    ? `<span class="badge bg-secondary ms-1">판매 종료</span>`
    : (isBeforeStart
    ?`<span class="badge bg-warning ms-1">수강 대기</span>`
    :"");
    //재생버튼
    const playButtonDisabled = (!isInProgress)
    ? 'style="pointer-events:none; opacity:0.5;"'
    : "";
    const viewLinkStart = (!isInProgress)
    ? `<a href="javascript:void(0);" onclick="alert('현재 시점에는 수강이 불가능합니다.'); return false;">`
    : `<a href="/lecture/view/\${r.lectureId}">`;
    // 예약취소 버튼 제어
    let buttonHTML = "";
    if (isLectureFinished) {
    buttonHTML = `<button class="btn btn-sm btn-secondary" disabled>강의 종료</button>`;
} else if (isInProgress) {
    buttonHTML = `<button class="btn btn-sm btn-outline-secondary" disabled>예약 취소</button>`;
} else {
    buttonHTML = `
                            <button class="btn-unreserve btn-main"
                                data-lecture-id="\${r.lectureId}"
                                data-bs-toggle="modal"
                                data-bs-target="#reserveModal">
                                예약 취소
                            </button>`;
}
    html += `
 <div class="card">
                <!-- 썸네일 영역 -->
                <div class="card-img-wrap">
                    \${viewLinkStart}
                        <img src="\${r.thumbnailUrl}" class="card-img-top" alt="\${r.title}">
                        <button class="play-btn" \${playButtonDisabled}>
                            <i class="bi bi-play-fill"></i>
                        </button>
                    </a>
                </div>

                <!-- 본문 -->
                <div class="card-body">
                    <a href="/lecture/content/\${r.lectureId}">
                        <h6 class="fw-bold text-ellipsis-2">\${r.title} \${badgeHTML}</h6>
                        <p class="text-muted">\${r.tutorName}</p>
                        <div class="progress" style="height:8px;">
                            <div class="progress-bar bg-success" style="width:\${r.progressPercent}%;"></div>
                        </div>
                        <small class="text-muted">\${r.progressPercent}%</small>
                    </a>
                </div>

                <!-- 푸터 (버튼 영역) -->
                <div class="card-footer">
                    <div class="button-wrap">
                        \${buttonHTML}
                        <a href="/lecture/content/\${r.lectureId}#review" class="btn-cancel">
                            수강평 작성
                        </a>
                    </div>
                </div>
            </div>
                    `;
});
    container.innerHTML = html;
},
    error: function (xhr) {
    console.error(xhr);
    alert("검색 중 오류가 발생했습니다.");
},
});
});
