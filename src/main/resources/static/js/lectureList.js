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
let currentKeyword = null;

// ✅ 무료/최신/인기 필터 select
const filterSelect = document.getElementById("filterSelect");
if (filterSelect) {
    filterSelect.addEventListener("change", () => {
        const selected = filterSelect.value;
        const params = new URLSearchParams(window.location.search);
        if (selected) params.set("filter", selected);
        else params.delete("filter");
        window.location.href = "?" + params.toString();
    });
}

// ✅ 세부분류 옵션 변경
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

// ✅ 세부분류 선택 시
subSelect.addEventListener("change", function () {
    const mainCategory = mainSelect.value;
    const subCategory = this.value;
    fetchLectures(mainCategory, subCategory, 0);
});

// ✅ 비동기 강좌 불러오기
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

    } catch (e) {
        console.error("강좌 목록 불러오기 실패:", e);
    }
}

// ✅ 렉처 카드 렌더링 (list.jsp 동일)
function renderLectures(lectures) {
    gridContainer.innerHTML = "";
    if (!lectures || lectures.length === 0) {
        gridContainer.innerHTML = "<div class='text-center p-5 w-100'><h5>검색 결과가 없습니다.</h5></div>";
        return;
    }

    lectures.forEach(lecture => {
        const statusBadge = (() => {
            if (lecture.status === "CLOSED" || lecture.status === "ENDED") {
                const label = lecture.status === "CLOSED" ? "예약 마감" : "강의 종료";
                return `<button type="button" class="badge bg-secondary flex-shrink-0" disabled style="width:max-content">${label}</button>`;
            } else {
                return `<button type="button" class="badge bg-success" style="width:max-content">예약 가능</button>`;
            }
        })();

        const card = document.createElement("a");
        card.href = `/lecture/content/${lecture.lectureId}`;
        card.className = "card popular-card";

        card.innerHTML = `
      <div class="card-thumb" style="height:180px;border-radius:12px 12px 0 0;overflow:hidden;">
        <img src="${lecture.thumbnailUrl || '/img/common/no-image.png'}"
             onerror="this.src='/img/common/no-image.png';"
             class="img-fluid rounded shadow-sm border"
             style="height:100%;object-fit:cover;">
      </div>
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
    `;

        gridContainer.appendChild(card);
    });
}

// ✅ 페이지네이션 렌더링 (list.jsp 동일)
function renderPagination(totalPages, currentPage, mainCategory, subCategory) {
    paginationContainer.innerHTML = "";
    if (totalPages <= 1) return;

    const pageGroupSize = 5;
    const currentGroup = Math.floor(currentPage / pageGroupSize);
    const startPage = currentGroup * pageGroupSize;
    let endPage = startPage + pageGroupSize - 1;
    if (endPage >= totalPages) endPage = totalPages - 1;

    const ul = document.createElement("ul");
    ul.className = "pagination justify-content-center";

    // ◀ 이전
    if (currentPage > 0) {
        const prevLi = document.createElement("li");
        prevLi.className = "page-item";
        prevLi.innerHTML = `<a class="page-link" href="#"><i class="bi bi-chevron-left"></i></a>`;
        prevLi.onclick = () => fetchLectures(mainCategory, subCategory, currentPage - 1);
        ul.appendChild(prevLi);
    }

    // 페이지 번호 (1-based 표시)
    for (let i = startPage; i <= endPage; i++) {
        const li = document.createElement("li");
        li.className = `page-item ${i === currentPage ? "active" : ""}`;
        li.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
        li.onclick = () => fetchLectures(mainCategory, subCategory, i, currentKeyword);
        ul.appendChild(li);
    }

    // ▶ 다음
    if (currentPage < totalPages - 1) {
        const nextLi = document.createElement("li");
        nextLi.className = "page-item";
        nextLi.innerHTML = `<a class="page-link" href="#"><i class="bi bi-chevron-right"></i></a>`;
        nextLi.onclick = () => fetchLectures(mainCategory, subCategory, currentPage + 1);
        ul.appendChild(nextLi);
    }

    paginationContainer.appendChild(ul);
}

// ✅ 초기 로드
document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const mainCategory = params.get("mainCategory");
    const keyword = params.get("keyword");

    // 기본 검색 유지
    if (keyword) {
        const input = document.querySelector("input[name='keyword']");
        if (input) input.value = keyword;
        fetchLectures(null, null, 0, keyword);
        return;
    }

    // 카테고리 유지
    if (mainCategory) {
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

    // 검색창 이벤트
    const searchForm = document.getElementById("searchForm");
    const keywordInput = searchForm.querySelector("input[name='keyword']");
    searchForm.addEventListener("submit", e => {
        e.preventDefault();
        const mainCategory = mainSelect.value;
        const subCategory = subSelect.value;
        const keyword = keywordInput.value.trim();
        fetchLectures(mainCategory, subCategory, 0, keyword);
    });
});
