const subCategories = {
    1: ["프론트엔드", "백엔드", "데이터베이스", "인공지능(AI)", "클라우드 / DevOps", "모바일 앱개발"],
    2: ["시간관리", "리더십", "생산성 향상", "자기소개서 / 면접"],
    3: ["사진 / 영상편집", "음악 / 작곡", "그림 / 디자인"],
    4: ["요리 / 베이킹", "피트니스", "요가 / 명상"],
    5: ["영어회화", "일본어", "중국어", "한국어"],
    6: ["심리학", "철학 / 역사", "정치 / 사회"],
    7: ["정보처리기사", "SQLD / ADsP", "토익 / 토플", "컴퓨터활용능력"],
    8: ["주식 / 투자", "부동산", "회계 / 재무관리"]
};

const mainSelect = document.getElementById("mainCategory");
const subSelect = document.getElementById("subCategory");
const gridContainer = document.querySelector(".recommend-grid");
const paginationContainer = document.querySelector(".pagination-wrap");

mainSelect.addEventListener("change", function () {
    const selected = this.value;
    subSelect.innerHTML = '<option value="">세부분류</option>';

    if (subCategories[selected]) {
        subCategories[selected].forEach(sub => {
            const opt = document.createElement("option");
            opt.value = sub;
            opt.textContent = sub;
            subSelect.appendChild(opt);
        });
    }
    fetchLectures(selected, null, 0);
});

subSelect.addEventListener("change", function () {
    const mainCategory = mainSelect.value;
    const subCategory = this.value;
    fetchLectures(mainCategory, subCategory, 0);
});

let currentKeyword = null;

async function fetchLectures(mainCategory, subCategory, page = 0, keyword = null) {
    try {
        if (keyword !== null) currentKeyword = keyword;

        const params = new URLSearchParams();
        if (mainCategory) params.append("mainCategory", mainCategory);
        if (subCategory) params.append("subCategory", subCategory);
        if (currentKeyword) params.append("keyword", currentKeyword);
        params.append("page", page);

        const response = await fetch(`/lecture/filter?${params.toString()}`);
        if (!response.ok) throw new Error("서버 응답 오류");

        const data = await response.json();
        renderLectures(data.lectures);
        renderPagination(data.totalPages, data.currentPage, mainCategory, subCategory);
    } catch (error) {
        console.error("강좌 목록 불러오기 실패:", error);
    }
}

// ✅ list.jsp와 동일한 강좌 카드 UI
function renderLectures(lectures) {
    gridContainer.innerHTML = "";

    if (!lectures || lectures.length === 0) {
        gridContainer.innerHTML = "<div class='text-center p-5 w-100'><h5>검색 결과가 없습니다.</h5></div>";
        return;
    }

    lectures.forEach(lecture => {
        const statusBadge = (() => {
            if (lecture.status === "CLOSED") return `<button type="button" class="badge bg-secondary flex-shrink-0" disabled style="width:max-content">예약 마감</button>`;
            if (lecture.status === "ENDED") return `<button type="button" class="badge bg-secondary flex-shrink-0" disabled style="width:max-content">강의 종료</button>`;
            return `<button type="button" class="badge bg-success" style="width:max-content">예약 가능</button>`;
        })();

        const cardHTML = `
        <a href="/lecture/content/${lecture.lectureId}" class="card popular-card">
            <!-- 썸네일 -->
            <div class="card-thumb" style="height:180px; border-radius:12px 12px 0 0; overflow:hidden;">
                <img src="${lecture.thumbnailUrl || '/img/common/no-image.png'}"
                     onerror="this.src='/img/common/no-image.png';"
                     alt="lecture thumbnail"
                     class="img-fluid rounded shadow-sm border"
                     style="height:100%; object-fit:cover;">
            </div>

            <!-- 강좌정보 -->
            <div class="card-body justify-content-between" style="gap:0;">
                <div class="d-flex align-items-center justify-content-between mb-2">
                    <h6 class="fw-bold text-ellipsis-2 mb-0 flex-grow-1 lh-base">
                        ${lecture.title}
                        ${statusBadge}
                    </h6>
                </div>
                <p class="text-muted mb-2">${lecture.tutorName}</p>
                <span class="mb-2">${(lecture.price ?? 0).toLocaleString()}원</span>
                <div class="card-review">
                    <div>
                        <span>⭐ ${(lecture.avgStar ?? 0).toFixed(1)}</span>
                        <span>(${lecture.reviewCount ?? 0})</span>
                    </div>
                    <div>
                        <i class="bi bi-person-fill"></i>
                        <span>${lecture.reservationCount ?? 0}</span>
                    </div>
                </div>
            </div>
        </a>`;
        gridContainer.insertAdjacentHTML("beforeend", cardHTML);
    });
}

// ✅ list.jsp의 JSP 페이징 로직 그대로 복제
function renderPagination(totalPages, currentPage, mainCategory, subCategory) {
    paginationContainer.innerHTML = "";

    if (totalPages <= 1) return;

    const pageGroupSize = 5;
    const current = currentPage + 1; // 0-based → 1-based
    const total = totalPages;

    const currentGroup = Math.floor((current - 1) / pageGroupSize);
    const startPage = currentGroup * pageGroupSize + 1;
    let endPage = startPage + pageGroupSize - 1;
    if (endPage > total) endPage = total;

    const ul = document.createElement("ul");
    ul.className = "pagination justify-content-center";

    // ◀ 이전
    if (currentPage > 0) {
        const prev = document.createElement("li");
        prev.className = "page-item";
        prev.innerHTML = `<a class="page-link" href="#"><i class="bi bi-chevron-left"></i></a>`;
        prev.onclick = () => fetchLectures(mainCategory, subCategory, currentPage - 1, currentKeyword);
        ul.appendChild(prev);
    }

    // 🔢 페이지 그룹
    for (let i = startPage; i <= endPage; i++) {
        const li = document.createElement("li");
        li.className = `page-item ${i === current ? "active" : ""}`;
        li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        li.onclick = () => fetchLectures(mainCategory, subCategory, i - 1, currentKeyword);
        ul.appendChild(li);
    }

    // ▶ 다음
    if (currentPage < totalPages - 1) {
        const next = document.createElement("li");
        next.className = "page-item";
        next.innerHTML = `<a class="page-link" href="#"><i class="bi bi-chevron-right"></i></a>`;
        next.onclick = () => fetchLectures(mainCategory, subCategory, currentPage + 1, currentKeyword);
        ul.appendChild(next);
    }

    const nav = document.createElement("nav");
    nav.className = "pagination-wrap mt-5";
    nav.appendChild(ul);

    paginationContainer.appendChild(nav);
}

document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const mainCategory = params.get("mainCategory");
    const keyword = params.get("keyword");

    if (keyword) {
        const keywordInput = document.querySelector("input[name='keyword']");
        if (keywordInput) keywordInput.value = keyword;
        fetchLectures(null, null, 0, keyword);
        return;
    }

    if (mainCategory && !keyword) {
        mainSelect.value = mainCategory;

        subSelect.innerHTML = '<option value="">세부분류</option>';
        if (subCategories[mainCategory]) {
            subCategories[mainCategory].forEach(sub => {
                const opt = document.createElement("option");
                opt.value = sub;
                opt.textContent = sub;
                subSelect.appendChild(opt);
            });
        }

        fetchLectures(mainCategory, null, 0);
    }

    const searchForm = document.getElementById("searchForm");
    const keywordInput = searchForm.querySelector("input[name='keyword']");

    searchForm.addEventListener("submit", function (e) {
        e.preventDefault();
        const mainCategory = mainSelect.value;
        const subCategory = subSelect.value;
        const keyword = keywordInput.value.trim();
        fetchLectures(mainCategory, subCategory, 0, keyword);
    });
});
