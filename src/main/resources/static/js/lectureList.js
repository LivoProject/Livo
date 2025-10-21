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

// ✅ 엘리먼트 선택
const mainSelect = document.getElementById("mainCategory");
const subSelect = document.getElementById("subCategory");
const gridContainer = document.querySelector(".recommend-grid");

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

    // ✅ 주제 선택만 해도 비동기 요청 실행
    fetchLectures(selected, null);
});

// ✅ 세부분류 선택 시 필터링 요청
subSelect.addEventListener("change", function () {
    const mainCategory = mainSelect.value;
    const subCategory = this.value;
    fetchLectures(mainCategory, subCategory);
});

// ✅ 비동기 요청으로 강좌 목록 가져오기
async function fetchLectures(mainCategory, subCategory) {
    try {
        const params = new URLSearchParams();
        if (mainCategory) params.append("mainCategory", mainCategory);
        if (subCategory) params.append("subCategory", subCategory);

        const response = await fetch(`/lecture/filter?${params.toString()}`);
        if (!response.ok) throw new Error("서버 응답 오류");

        const lectures = await response.json();
        renderLectures(lectures);
    } catch (error) {
        console.error("강좌 목록 불러오기 실패:", error);
    }
}

// ✅ 받아온 강좌 리스트를 HTML로 렌더링
function renderLectures(lectures) {
    gridContainer.innerHTML = ""; // 기존 리스트 초기화

    if (lectures.length === 0) {
        gridContainer.innerHTML = "<p class='text-center text-muted'>해당 조건의 강좌가 없습니다.</p>";
        return;
    }

    lectures.forEach(lecture => {
        const card = document.createElement("a");
        card.href = `/lecture/content/${lecture.lectureId}`;
        card.className = "card popular-card";
        card.innerHTML = `
      <div class="card-thumb"
           style="background-image: url('/img/lecture/lecture_${lecture.lectureId}.jpg');
                  background-size: cover; background-position: center;
                  height: 200px; border-radius: 12px 12px 0 0;">
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
      </div>
    `;
        gridContainer.appendChild(card);
    });
}
