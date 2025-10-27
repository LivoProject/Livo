// ✅ 세부분류 목록 (주제별)
const subCategories = {
    1: ["프론트엔드", "백엔드", "데이터베이스", "인공지능(AI)", "클라우드/DevOps", "모바일 앱개발"],
    2: ["시간관리", "리더십", "생산성 향상", "자기소개서/면접"],
    3: ["사진/영상편집", "음악/작곡", "그림/디자인"],
    4: ["요리/베이킹", "피트니스", "요가/명상"],
    5: ["영어회화", "일본어", "중국어", "한국어"],
    6: ["심리학", "철학/역사", "정치/사회"],
    7: ["정보처리기사", "SQLD/ADsP", "토익/토플", "컴퓨터활용능력"],
    8: ["주식/투자", "부동산", "회계/재무관리"]
};

const mainSelect = document.getElementById("mainCategory");
const subSelect = document.getElementById("subCategory");
const gridContainer = document.querySelector(".recommend-grid");
const paginationContainer = document.querySelector(".pagination");

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

// ✅ 강좌 불러오기 (비동기)
async function fetchLectures(mainCategory, subCategory, page = 0) {
    try {
        const params = new URLSearchParams();
        if (mainCategory) params.append("mainCategory", mainCategory);
        if (subCategory) params.append("subCategory", subCategory);
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
        gridContainer.innerHTML = "<p class='text-center text-muted'>해당 조건의 강좌가 없습니다.</p>";
        return;
    }

    lectures.forEach(lecture => {
        const card = document.createElement("a");
        card.href = `/lecture/content/${lecture.lectureId}`;
        card.className = "card popular-card";
        card.innerHTML = `
        <div class="card-thumb" style="height: 200px; border-radius: 12px 12px 0 0; overflow: hidden;">
            <img src="${lecture.thumbnailUrl || '/img/common/no-image.png'}"
                onerror="this.src='/img/common/no-image.png';"
                alt="lecture thumbnail"
                class="img-fluid rounded shadow-sm border"
                style="max-height: 280px; object-fit: cover;">
        </div>
        <div class="card-body">
            <h6>${lecture.title}</h6>
            <p>${lecture.tutorName} ∣ ${lecture.price.toLocaleString()}원</p>
            <div class="card-review d-flex justify-content-between">
                <div>
                    <span>⭐ ${lecture.avgStar?.toFixed(1) ?? "0.0"}</span>
                    <span>(${lecture.reviewCount ?? 0})</span>
                </div>
                <div>
                    <i class="bi bi-person-fill"></i>
                    <span>${lecture.reservationCount ?? 0}</span>
                </div>
            </div>
        </div>`;
        gridContainer.appendChild(card);
    });
}

// ✅ 페이지네이션 렌더링
function renderPagination(totalPages, currentPage, mainCategory, subCategory) {
    paginationContainer.innerHTML = "";

    if (totalPages <= 1) return;

    const ul = document.createElement("ul");
    ul.className = "pagination justify-content-center mt-4";

    // 이전
    if (currentPage > 0) {
        const prevLi = document.createElement("li");
        prevLi.className = "page-item";
        prevLi.innerHTML = `<a class="page-link" href="#">이전</a>`;
        prevLi.onclick = () => fetchLectures(mainCategory, subCategory, currentPage - 1);
        ul.appendChild(prevLi);
    }

    // 페이지 번호
    for (let i = 0; i < totalPages; i++) {
        const li = document.createElement("li");
        li.className = `page-item ${i === currentPage ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="#">${i + 1}</a>`;
        li.onclick = () => fetchLectures(mainCategory, subCategory, i);
        ul.appendChild(li);
    }

    // 다음
    if (currentPage < totalPages - 1) {
        const nextLi = document.createElement("li");
        nextLi.className = "page-item";
        nextLi.innerHTML = `<a class="page-link" href="#">다음</a>`;
        nextLi.onclick = () => fetchLectures(mainCategory, subCategory, currentPage + 1);
        ul.appendChild(nextLi);
    }

    paginationContainer.appendChild(ul);
}
