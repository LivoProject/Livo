<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<script src="/js/mypage.js"></script>


<!-- 컨텐츠-->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 강의 -->
    <main class="main-content">
        <h3 class="mt-0 mb-0">내 강의실</h3>

        <div class="d-flex flex-column mb-3 justify-content-end align-items-end">

            <!-- 검색폼 -->
            <form id="lectureSearchForm" class="input-group mb-2 w-50">
                <input type="text" name="keyword" id="keyword"
                       class="form-control" placeholder="강좌명, 강사명 입력">
                <button type="submit" id="searchLectureBtn" class="btn btn-main">
                    <i class="bi bi-search"></i>
                </button>
            </form>

            <!-- 정렬 선택 -->
            <select class="form-select w-25">
                <option selected>최신순</option>
                <option value="old">오래된순</option>
                <option value="popular">인기순</option>
            </select>
        </div>

        <!-- 강의 카드 목록 (Ajax 결과도 여기에 덮어씀) -->
        <div id="lectureContainer" class="lecture-grid large">
            <c:if test="${not empty reservations}">
                <c:forEach var="reservations" items="${reservations}">
                    <div class="card">
                        <div class="card-img-wrap">
                            <c:choose>
                                <c:when test="${reservations.lectureStatus eq 'ENDED'}">
                                    <a href="javascript:void(0);" onclick="alert('수강 기간이 종료된 강의입니다. 다시 수강을 원하시면 재결제 후 이용해주세요.'); return false;">
                                        <img src="${reservations.thumbnailUrl}" class="card-img-top" style="opacity:0.6; filter:grayscale(40%);" alt="강의 썸네일"/>
                                        <button class="play-btn" style="pointer-events:none; opacity:0.5;">
                                            <i class="bi bi-play-fill"></i>
                                        </button>
                                    </a>
                                </c:when>
                                <c:when test="${reservations.lectureStart gt today}">
                                    <a href="javascript:void(0);" onclick="alert('수강 시작일 이후부터 시청할 수 있습니다.'); return false;">
                                        <img src="${reservations.thumbnailUrl}" class="card-img-top" style="opacity:0.5;" alt="강의 썸네일"/>
                                        <button class="play-btn" style="pointer-events:none; opacity:0.5;">
                                            <i class="bi bi-play-fill"></i>
                                        </button>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="/lecture/view/${reservations.lectureId}">
                                        <img src="${reservations.thumbnailUrl}" class="card-img-top" alt="강의 썸네일"/>
                                        <button class="play-btn">
                                            <i class="bi bi-play-fill"></i>
                                        </button>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="card-body">
                            <a href="/lecture/content/${reservations.lectureId}">
                                <h6 class="fw-bold text-ellipsis-2 lecture-title">
                                    ${reservations.title}
                                    <c:if test="${reservations.visibility eq 'DELETED'}">
                                        <span class="badge bg-secondary ms-1">판매 종료</span>
                                    </c:if>
                                    <c:if test="${reservations.lectureStart gt today}">
                                        <span class="badge bg-warning ms-1">수강 대기</span>
                                    </c:if>
                                </h6>
                                <p class="text-muted">${reservations.tutorName}</p>
                                <div class="progress" style="height: 8px;">
                                    <div class="progress-bar bg-success"
                                         style="width: ${reservations.progressPercent}%;"></div>
                                </div>
                                <small class="text-muted">${reservations.progressPercent}%</small>
                            </a>
                        </div>
                        <div class="card-footer">
                            <div class="button-wrap">
                                <c:choose>
                                    <c:when test="${reservations.lectureStatus eq 'ENDED'}">
                                        <button class="btn btn-sm btn-secondary" disabled>수강 종료</button>
                                    </c:when>
                                    <c:when test="${reservations.lectureStart le today and reservations.lectureEnd ge today}">
                                        <button class="btn btn-sm btn-outline-secondary" disabled>예약 취소</button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn-unreserve btn-main"
                                                data-lecture-id="${reservations.lectureId}"
                                                data-price="${reservations.price}"
                                                data-bs-toggle="modal"
                                                data-bs-target="#reserveModal">
                                            예약 취소
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                                <a href="/lecture/content/${reservations.lectureId}#review" class="btn-cancel">
                                    수강평 작성
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>

            <c:if test="${empty reservations}">
                <p class="text-muted">예약한 강의가 없습니다.</p>
            </c:if>
        </div>

        <%@ include file="/WEB-INF/views/common/pagination.jsp" %>

    </main>

</section>

<!-- 컨텐츠 끝 -->
<script>
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
                    const isLectureFinished = r.lectureStatus === 'ENDED'; //수강기간종료
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
                    // 예약취소 버튼 제어
                    /*const buttonHTML = isLectureFinished
                        ? `<button class="btn btn-sm btn-secondary" disabled>수강 종료됨</button>`
                        : `<button class="btn-unreserve btn-main" data-lecture-id="\${r.lectureId}" data-bs-toggle="modal" data-bs-target="#reserveModal">예약 취소</button>`;*/
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
</script>


<!-- 모달 -->
<%@ include file="/WEB-INF/views/common/modal.jsp" %>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>