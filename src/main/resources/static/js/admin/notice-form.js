(function () {
    // 요소 선택 (안전 가드)
    const form           = document.getElementById("noticeForm");
    const titleEl        = document.getElementById("title");
    const titleCountEl   = document.getElementById("titleCount");
    const contentEl      = document.getElementById("summernote");
    const pinnedEl       = document.getElementById("pinned");
    const visibleEl      = document.getElementById("visible"); // 있을 수도, 없을 수도

    const btnPreview     = document.getElementById("btnPreview");
    const previewLayer   = document.getElementById("previewLayer");
    const btnClosePreview= document.getElementById("btnClosePreview");

    const pvTitle        = document.getElementById("pvTitle");
    const pvPinned       = document.getElementById("pvPinned");
    const pvContent      = document.getElementById("pvContent");

    if (!form || !titleEl || !contentEl) return;

    //썸머노트 초기화
    $(document).ready(function() {
        if ($('#summernote').length) {
            $('#summernote').summernote({
                height: 350,
                placeholder: '공지 내용을 입력하세요.',
                codeviewFilter: true,
                codeviewIframeFilter: true,
                lang: 'ko-KR',
                lineHeights: ['0.8', '1.0', '1.2', '1.4', '1.6', '2.0', '3.0'],
                toolbar: [
                    ['style', ['bold', 'italic', 'underline', 'clear']],
                    ['font', ['fontsize', 'color']],
                    ['para', ['ul', 'ol', 'paragraph']],
                    ['height', ['lineHeight']]
                ]
            });
        }
    });
    /** 유틸: HTML escape */
    function escapeHTML(str) {
        return (str || "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }
    /** 유틸: 줄바꿈 유지 */
    function nl2br(str) {
        return escapeHTML(str).replace(/\r?\n/g, "<br>");
    }

    /** ============================
     * 제목 글자수 카운트 (붙여넣기/IME 포함)
     * ============================ */
    const updateTitleCount = () => {
        if (!titleCountEl) return;
        titleCountEl.textContent = (titleEl.value || "").length;
    };
    titleEl.addEventListener("input", updateTitleCount);
    titleEl.addEventListener("compositionend", updateTitleCount);
    document.addEventListener("paste", (e) => {
        if (e.target === titleEl) {
            // paste 후 렌더 타이밍 고려
            setTimeout(updateTitleCount, 0);
        }
    });
    updateTitleCount();

    /** ============================
     * 체크박스 미체크 시 값 누락 방지
     *   (unchecked면 name 자체가 안 가므로 hidden을 추가)
     * ============================ */
    function ensureBoolean(name, checked) {
        // 이미 같은 name의 hidden이 있으면 제거 후 재추가(중복 방지)
        const existing = form.querySelector(`input[type="hidden"][name="${name}"]`);
        if (existing) existing.remove();

        if (!checked) {
            const h = document.createElement("input");
            h.type = "hidden";
            h.name = name;
            h.value = "false";
            form.appendChild(h);
        } else {
            // checked면 기본 체크박스가 "on"으로 가고, 스프링에서 true로 바인딩됨
        }
    }

    /** ============================
     * 폼 유효성 + 이중 제출 방지
     * ============================ */
    let submitting = false;
    form.addEventListener("submit", function (e) {
        const title = (titleEl.value || "").trim();
        const content = (contentEl.value || "").trim();

        if (!title) {
            e.preventDefault();
            alert("제목을 입력하세요.");
            titleEl.focus();
            return;
        }
        if (!content) {
            e.preventDefault();
            alert("내용을 입력하세요.");
            contentEl.focus();
            return;
        }

        // 체크박스 바인딩 보강
        if (pinnedEl)  ensureBoolean("pinned",  pinnedEl.checked);
        if (visibleEl) ensureBoolean("visible", visibleEl.checked);

        // 이중 제출 방지
        if (submitting) {
            e.preventDefault();
            return;
        }
        submitting = true;
        const submitBtn = form.querySelector('button[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = submitBtn.textContent.replace(/등록|수정 저장/g, "$& 중...");
        }
    });

    /** ============================
     * 미리보기
     * ============================ */
    if (btnPreview && previewLayer && pvTitle && pvContent && pvPinned) {
        let prevFocused = null;

        function openPreview() {
            const title = (titleEl.value || "").trim();
            const content = (contentEl.value || "").trim();
            const pinned = pinnedEl ? pinnedEl.checked : false;

            pvTitle.textContent = title || "(제목 없음)";
            pvContent.innerHTML = content ? nl2br(content) : "<em>(내용 없음)</em>";
            pvPinned.style.display = pinned ? "block" : "none";

            prevFocused = document.activeElement;
            previewLayer.classList.remove("hidden");
            if (btnClosePreview) btnClosePreview.focus();
        }

        function closePreview() {
            previewLayer.classList.add("hidden");
            if (prevFocused && typeof prevFocused.focus === "function") {
                prevFocused.focus();
            }
        }

        btnPreview.addEventListener("click", openPreview);
        if (btnClosePreview) btnClosePreview.addEventListener("click", closePreview);
        previewLayer.addEventListener("click", function (e) {
            if (e.target === previewLayer) closePreview();
        });
        document.addEventListener("keydown", function (e) {
            if (!previewLayer.classList.contains("hidden") && e.key === "Escape") {
                closePreview();
            }
        });
    }
})();

// 제목 글자수 초기화(수정 모드에서 서버 값 기준)
(function(){
    var t = document.getElementById('title');
    if (!t) return;
    var c = document.getElementById('titleCount');
    var update = function(){ c.textContent = (t.value || '').length; };
    t.addEventListener('input', update);
    update();
})();
