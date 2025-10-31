<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Navbar -->
<nav class="navbar navbar-main navbar-expand-lg px-0 mx-4 shadow-none border-radius-xl" id="navbarBlur" navbar-scroll="true">
    <div class="container-fluid py-1 px-4">
        <!--  모바일 전용 버튼-->
        <button class="btn btn-outline-secondary d-lg-none mb-0" type="button" data-bs-toggle="offcanvas" data-bs-target="#sidebarMobile">
            <i class="bi bi-list fs-4"></i>
        </button>
        <div class="collapse navbar-collapse mt-sm-0 mt-2 me-md-0 me-sm-4" id="navbar">
            <div class="ms-md-auto pe-md-3 d-flex align-items-center">
<%--                <div class="input-group">--%>
<%--                    <span class="input-group-text text-body"><i class="fas fa-search" aria-hidden="true"></i></span>--%>
<%--                    <input type="text" class="form-control" placeholder="Type here...">--%>
<%--                </div>--%>
            </div>
            <ul class="navbar-nav justify-content-end">
                <li class="nav-item d-flex align-items-center">
                    <!--  로그아웃: 반드시 POST + CSRF -->
                    <form action="${pageContext.request.contextPath}/auth/logout" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <button type="submit" class="btn btn-outline-danger btn-sm px-3">
                            <i class="fa fa-sign-out me-sm-1"></i>
                            <span class="d-sm-inline d-none">Logout</span>
                        </button>
                    </form>
                </li>
            </ul>

        </div>
    </div>
</nav>
