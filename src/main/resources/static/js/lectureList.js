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
const paginationContainer = document.querySelector(".pagination-wrap"); // ✅ nav 요소 선택으로 변경

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

// ✅ 세부분류 선택 시 필터링 요청
subSelect.addEventListener("change", function () {
    const mainCategory = mainSelect.value;
    const subCategory = this.value;
    fetchLectures(mainCategory, subCategory, 0);
});

let currentKeyword = null; // ✅ 전역변수 추가

// ✅ 강좌 불러오기 (비동기)
async function fetchLectures(mainCategory, subCategory, page = 0, keyword = null) {
    try {
        // keyword가 새로 입력된 경우에만 currentKeyword 갱신
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

// ✅ 렉처 렌더링 (UI 동일)
function renderLectures(lectures) {
    gridContainer.innerHTML = "";

    if (!lectures || lectures.length === 0) {
        gridContainer.innerHTML = "<div class='text-center p-5 w-100'><h5>검색 결과가 없습니다.</h5></div>";
        return;
    }

    lectures.forEach(lecture => {
        const card = document.createElement("a");
        card.href = `/lecture/content/${lecture.lectureId}`;
        card.className = "card popular-card";

        card.innerHTML = `
          <!-- 썸네일 -->
          <div class="card-thumb" style="height: 200px; border-radius: 12px 12px 0 0; overflow: hidden;">
            <img src="${lecture.thumbnailUrl || '/img/common/no-image.png'}"
                 onerror="this.src='/img/common/no-image.png';"
                 alt="lecture thumbnail"
                 class="img-fluid rounded shadow-sm border"
                 style="max-height: 280px; object-fit: cover;">
          </div>

          <!-- 강좌정보 -->
          <div class="card-body">
            <h6 class="fw-bold mb-2 text-ellipsis-2">${lecture.title}</h6>
            <p class="text-muted mb-3">${lecture.tutorName}</p>
            <span>${(lecture.price ?? 0).toLocaleString()}원</span>
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

// ✅ 페이지네이션 렌더링 (pagination.jsp 스타일 그대로 복제)
function renderPagination(totalPages, currentPage, mainCategory, subCategory) {
    paginationContainer.innerHTML = "";

    if (totalPages <= 1) return;

    const nav = document.createElement("nav");
    nav.className = "pagination-wrap mt-4";

    const ul = document.createElement("ul");
    ul.className = "pagination justify-content-center";

    // ⬅ 이전 버튼
    if (currentPage > 0) {
        const prevLi = document.createElement("li");
        prevLi.className = "page-item";
        prevLi.innerHTML = `
            <a class="page-link" href="#">
                <i class="bi bi-chevron-left"></i>
            </a>`;
        prevLi.onclick = () => fetchLectures(mainCategory, subCategory, currentPage - 1);
        ul.appendChild(prevLi);
    }

    // 🔢 페이지 번호
    for (let i = 0; i < totalPages; i++) {
        const li = document.createElement("li");
        li.className = `page-item ${i === currentPage ? "active" : ""}`;
        li.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
        li.onclick = () => fetchLectures(mainCategory, subCategory, i, currentKeyword);
        ul.appendChild(li);
    }

    // ➡ 다음 버튼
    if (currentPage < totalPages - 1) {
        const nextLi = document.createElement("li");
        nextLi.className = "page-item next";
        nextLi.innerHTML = `
            <a class="page-link" href="#">
                <i class="bi bi-chevron-right"></i>
            </a>`;
        nextLi.onclick = () => fetchLectures(mainCategory, subCategory, currentPage + 1);
        ul.appendChild(nextLi);
    }

    nav.appendChild(ul);
    paginationContainer.appendChild(nav);
}

// ✅ 페이지 로드시 mainCategory 파라미터가 있으면 자동으로 로드
document.addEventListener("DOMContentLoaded", function () {
    const params = new URLSearchParams(window.location.search);
    const mainCategory = params.get("mainCategory");

    const keyword = params.get("keyword"); // ✅ header.jsp에서 넘어온 검색어 감지

    if (keyword) { // ✅ keyword가 있을 때 바로 검색 실행
        const keywordInput = document.querySelector("input[name='keyword']");
        if (keywordInput) keywordInput.value = keyword; // 검색창에 값 유지
        fetchLectures(null, null, 0, keyword); // 비동기 검색 실행
        return; // ✅ 중복 실행 방지
    }

    // 초기 로드 시 mainCategory 파라미터가 있으면 자동 선택 + 세부분류 로드
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

    // ✅ (추가) 검색 기능
    const searchForm = document.getElementById("searchForm");
    const keywordInput = searchForm.querySelector("input[name='keyword']");

    searchForm.addEventListener("submit", function (e) {
        e.preventDefault(); // 기본 form 제출 막기 (새로고침 방지)

        const mainCategory = mainSelect.value;
        const subCategory = subSelect.value;
        const keyword = keywordInput.value.trim();

        fetchLectures(mainCategory, subCategory, 0, keyword);
    });
});

