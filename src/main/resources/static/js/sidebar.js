document.addEventListener("DOMContentLoaded", function() {
    const links = document.querySelectorAll("#sidebarMenu .nav-link");
    const currentPath = window.location.pathname; // 현재 URL 경로
    console.log(currentPath);
    links.forEach(link => {
        // 링크의 href와 현재 URL 비교
        if (link.getAttribute("href") === currentPath || currentPath.endsWith(link.getAttribute("href"))) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });

    //무료강의 체크박스 관련
    const priceInput = document.getElementById("price"); // 강의비 input
    const freeCheck = document.getElementById("isFree"); // 무료 체크

    freeCheck.addEventListener("change", () => {
        if(freeCheck.checked) {
            priceInput.value = 0;
            priceInput.disabled = true;
        } else {
            priceInput.disabled = false;
        }
    });

});