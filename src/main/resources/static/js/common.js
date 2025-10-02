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
    { threshold: 0.2 }
  );

  fadeElems.forEach((el) => {
    observer.observe(el);
  });

});
