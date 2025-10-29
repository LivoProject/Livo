<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- 사이드 메뉴 -->
<div class="side-menu">
    <div class="profile">
        <a href="#" class="dropdown-toggle d-flex align-items-center" data-bs-toggle="dropdown" style="gap: 0.4rem">
            <%-- 소셜 로그인 --%>
            <sec:authorize access="principal instanceof T(org.springframework.security.oauth2.core.user.OAuth2User)">
                <sec:authentication var="gName" property="principal.attributes['name']"/>
                <sec:authentication var="gEmail" property="principal.attributes['email']"/>
                <sec:authentication var="gPic" property="principal.attributes['picture']"/>
                <c:if test="${not empty gPic}">
                    <img src="${gPic}" alt="profile" style="width:35px;height:35px;border-radius:50%;vertical-align:middle;margin-right:6px;">
                </c:if>
            </sec:authorize>
            <%-- 일반 로그인 --%>
            <sec:authorize
                    access="!(principal instanceof T(org.springframework.security.oauth2.core.user.OAuth2User))">
                <img
                    src="/img/common/profile-icon.svg"
                    alt=""
                    width="35"
                    height="35"
                    class="rounded-circle"
                />
            </sec:authorize>
            <strong>${mypage.nickname}</strong>
        </a>

        <ul class="dropdown-menu">
            <li>
                <button class="dropdown-item" onclick="document.getElementById('logoutForm').submit();">로그아웃</button>
            </li>
        </ul>
    </div>

    <hr class="my-4"/>

    <ul class="nav flex-column">
        <li><a href="${pageContext.request.contextPath}/mypage" class="nav-link ${menu eq 'home' ? 'active' : ''}">홈</a>
        </li>
        <li><a href="${pageContext.request.contextPath}/mypage/lecture"
               class="nav-link ${menu eq 'lecture' ? 'active' : ''}">내 강의실</a></li>
        <li><a href="${pageContext.request.contextPath}/mypage/info" class="nav-link ${menu eq 'info' ? 'active' : ''}">내
            정보 관리</a></li>
        <li><a href="${pageContext.request.contextPath}/mypage/like" class="nav-link ${menu eq 'like' ? 'active' : ''}">즐겨찾는
            강의</a></li>
        <li><a href="${pageContext.request.contextPath}/mypage/review"
               class="nav-link ${menu eq 'review' ? 'active' : ''}">수강평 관리</a></li>
        <li><a href="${pageContext.request.contextPath}/mypage/payment"
               class="nav-link ${menu eq 'payment' ? 'active' : ''}">결제 내역</a></li>
    </ul>

</div>