document.addEventListener("DOMContentLoaded", function () {

    // === 검색 아이콘 클릭 시 드롭다운 토글 === //
    const searchBtn = document.getElementById("searchToggle");
    if (searchBtn) {
        searchBtn.addEventListener("click", function () {
            let dropdown = document.getElementById("headerSearch");
            dropdown.classList.toggle("show");
        });
    }

    // === fade-in-up 애니메이션 === //
    const fadeElems = document.querySelectorAll(".fade-in-up");
    const observer = new IntersectionObserver(
        (entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    entry.target.classList.add("show");
                    observer.unobserve(entry.target); // 한 번만 실행
                }
            });
        },
        {threshold: 0.2}
    );

    fadeElems.forEach((el) => {
        observer.observe(el);
    });

    // === 푸터 챗봇 버튼 === //
    const fab = document.getElementById('chat-fab');
    const panel = document.getElementById('chat-panel');
    if (fab && panel) {
        fab.addEventListener('click', () => {
            panel.hidden = !panel.hidden;
            fab.setAttribute('aria-expanded', String(!panel.hidden));
        });
    }

    // === 푸터 스크롤 탑 버튼 === //
    const toTopBtn = document.getElementById("toTopBtn");
    toTopBtn.addEventListener("click", () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    });

    // ===  URL에 해시(#)가 있으면 자동으로 해당 탭 열기 === //
    const activateTabFromHash = () => {
        const hash = window.location.hash;
        if (hash) {
            const tabTrigger = document.querySelector(`a[data-bs-toggle="tab"][href="${hash}"]`);
            if (tabTrigger) {
                // 탭 전환 실행
                const tab = new bootstrap.Tab(tabTrigger);
                tab.show();
            }
        }
    };

    // DOM 로드 후 실행
    activateTabFromHash();

    // 해시 변경 감지 (뒤로가기/앞으로가기 대응)
    window.addEventListener("hashchange", activateTabFromHash);

    // 탭 전환 시 해시 갱신 (URL 유지)
    document.querySelectorAll('a[data-bs-toggle="tab"]').forEach(el => {
        el.addEventListener('shown.bs.tab', e => {
            history.replaceState(null, null, e.target.getAttribute('href'));
        });
    });
});
