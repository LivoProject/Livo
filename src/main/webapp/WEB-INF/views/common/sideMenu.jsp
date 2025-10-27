<%@ page contentType="text/html;charset=UTF-8" language="java" %>

  <!-- 사이드 메뉴 -->
  <div class="side-menu">
    <div class="profile">
      <a href="#" class="dropdown-toggle" data-bs-toggle="dropdown">
        <img
          src="https://github.com/mdo.png"
          alt=""
          width="32"
          height="32"
          class="rounded-circle"
        />
        <strong>수빈</strong>
      </a>

      <ul class="dropdown-menu">
        <li><a class="dropdown-item" href="#">프로필 변경</a></li>
        <li><button class="dropdown-item" onclick="document.getElementById('logoutForm').submit();">로그아웃</button></li>
      </ul>
    </div>

    <hr class="my-4" />

   <ul class="nav flex-column">
     <li><a href="${pageContext.request.contextPath}/mypage" class="nav-link ${menu eq 'home' ? 'active' : ''}">홈</a></li>
     <li><a href="${pageContext.request.contextPath}/mypage/lecture" class="nav-link ${menu eq 'lecture' ? 'active' : ''}">내 강의실</a></li>
     <li><a href="${pageContext.request.contextPath}/mypage/info" class="nav-link ${menu eq 'info' ? 'active' : ''}">내 정보 관리</a></li>
     <li><a href="${pageContext.request.contextPath}/mypage/like" class="nav-link ${menu eq 'like' ? 'active' : ''}">즐겨찾는 강의</a></li>
     <li><a href="${pageContext.request.contextPath}/mypage/review" class="nav-link ${menu eq 'review' ? 'active' : ''}">수강평 관리</a></li>
     <li><a href="${pageContext.request.contextPath}/mypage/payment" class="nav-link ${menu eq 'payment' ? 'active' : ''}">결제 내역</a></li>
   </ul>

  </div>