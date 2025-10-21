(function () {
    // 요소 선택
    const titleEl = document.getElementById("title");
    const titleCountEl = document.getElementById("titleCount");
    const contentEl = document.getElementById("content");
    const pinnedEl = document.getElementById("pinned");

    const btnPreview = document.getElementById("btnPreview");
    const previewLayer = document.getElementById("previewLayer");
    const btnClosePreview = document.getElementById("btnClosePreview");

    const pvTitle = document.getElementById("pvTitle");
    const pvPinned = document.getElementById("pvPinned");
    const pvContent = document.getElementById("pvContent");

    /** ============================
     * 제목 글자수 실시간 카운트
     * ============================ */
    function updateTitleCount() {
        const length = (titleEl.value || "").length;
        titleCountEl.textContent = length;
    }
    titleEl.addEventListener("input", updateTitleCount);
    updateTitleCount();

    /** ============================
     * 폼 유효성 검사
     * ============================ */
    document.getElementById("noticeForm").addEventListener("submit", function (e) {
        const title = (titleEl.value || "").trim();
        const content = (contentEl.value || "").trim();

        if (!title) {
            e.preventDefault();
            alert("제목을 입력하세요.");
            titleEl.focus();
            return false;
        }
        if (!content) {
            e.preventDefault();
            alert("내용을 입력하세요.");
            contentEl.focus();
            return false;
        }
        return true;
    });

    /** ============================
     * 미리보기 기능
     * ============================ */
    btnPreview.addEventListener("click", function () {
        const title = (titleEl.value || "").trim();
        const content = (contentEl.value || "").trim();
        const pinned = pinnedEl.checked;

        pvTitle.textContent = title || "(제목 없음)";
        pvContent.textContent = content || "(내용 없음)";
        pvPinned.style.display = pinned ? "block" : "none";

        previewLayer.classList.remove("hidden");
    });

    /** 닫기 버튼 */
    btnClosePreview.addEventListener("click", function () {
        previewLayer.classList.add("hidden");
    });

    /** 모달 배경 클릭 닫기 */
    previewLayer.addEventListener("click", function (e) {
        if (e.target === previewLayer) {
            previewLayer.classList.add("hidden");
        }
    });
})();
