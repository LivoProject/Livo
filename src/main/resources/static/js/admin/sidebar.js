document.addEventListener("DOMContentLoaded", function() {
    const links = document.querySelectorAll("#sidebarMenu .nav-link");
    const currentPath = window.location.pathname.split('/').slice(0, 3).join('/'); // ex) /admin/lecture

    links.forEach(link => {
        const href = link.getAttribute("href");
        if (href && currentPath.startsWith(href)) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }
    });
});

