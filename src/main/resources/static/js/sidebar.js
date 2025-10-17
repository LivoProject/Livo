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

});