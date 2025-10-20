document.addEventListener("DOMContentLoaded", () => {
    const keywordInput = document.getElementById("keyword");
    const searchButton = document.getElementById("searchButton");
    const container = document.getElementById("recommend");
    const template = container.getElementsByClassName(".container");

    searchButton.addEventListener("click", async () => {
        const keyword = keywordInput.value.trim();
        if (!keyword) {
            container.innerHTML = "<p>검색어를 입력하세요.</p>";
            return;
        }

        try {
            const response = await fetch(`/search?keyword=${encodeURIComponent(keyword)}`);
            const data = await response.json();

            // 기존 결과 초기화
            container.innerHTML = "";

            if (data.length === 0) {
                container.innerHTML = "<p>검색 결과가 없습니다.</p>";
                return;
            }

            // 템플릿 복제 및 데이터 주입
            data.forEach(item => {
                const clone = template.content.cloneNode(true);
                const card = clone.querySelector(".card");

                // 링크
                card.href = `/lecture/content/${item.id}`;

                // 썸네일
                const thumb = clone.querySelector(".card-thumb");
                thumb.style.backgroundImage = `url('/img/lecture/lecture_${item.id}.jpg')`;

                // 제목, 강사명, 수강자 수
                clone.querySelector(".title").textContent = item.title;
                clone.querySelector(".tutor").textContent = `${item.tutorName} ∣ ${item.price.toLocaleString()}원`;
                clone.querySelector(".reservation").textContent = item.reservationCount || 0;

                container.appendChild(clone);
            });
        } catch (error) {
            container.innerHTML = "<p>검색 중 오류가 발생했습니다.</p>";
            console.error(error);
        }
    });
});
