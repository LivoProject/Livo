<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">

<head>
  <meta charset="UTF-8" />
  <title>PROJECT</title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <!-- Bootstrap -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" />
  <!-- Swiper -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
  <!-- Custom CSS -->
  <link rel="stylesheet" href="/css/reset.css" />
  <link rel="stylesheet" href="/css/common.css" />
  <link rel="stylesheet" href="/css/main.css" />
  <link rel="stylesheet" href="/css/sub.css" />
  <!-- JS -->
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</head>

<body>

  <!-- 헤더 -->
  <header>
    <div class="container">
      <nav class="navbar navbar-expand-lg">
        <a class="logo navbar-brand" href="#">LOGO</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNavbar">
          <ul class="navbar-nav me-auto">
            <li class="nav-item"><a class="nav-link" href="#">홈</a></li>
            <li class="nav-item"><a class="nav-link" href="#">강좌</a></li>
            <li class="nav-item"><a class="nav-link" href="#">공지사항</a></li>
            <li class="nav-item"><a class="nav-link" href="#">마이페이지</a></li>
            <li><button id="searchToggle" class="nav-link"><i class="bi bi-search"></i></button></li>
          </ul>

          <div class="header-actions">
            <button><i class="bi bi-box-arrow-in-right"></i>로그인</button>
            <button><i class="bi bi-person-plus"></i>회원가입</button>
          </div>
        </div>
      </nav>
    </div>
  </header>

  <!-- 헤더 검색창 -->
  <div id="headerSearch">
    <h4>배우고 싶은 강좌를 찾아보세요.</h4>
    <div class="input-group">
      <input type="text" class="form-control" placeholder="강좌명, 대학명, 키워드 입력" />
      <button><i class="bi bi-search"></i></button>
    </div>
  </div>

  <!-- 민영페이지 시작 강좌 목록 -->
  <section id="sub" class="container mt-4">
    <h3>강좌 검색</h3>

    <!-- 검색바 -->
    <div class="p-3 text-bg-dark rounded-3 mb-4">
      <form action="/lecture/search" method="get"
            class="d-flex flex-nowrap align-items-center justify-content-center gap-2">

        <!-- 주제 선택 -->
        <select id="mainCategory" name="mainCategory" class="form-select w-auto">
          <option value="">주제 선택</option>
          <option value="1">IT</option>
          <option value="2">자기계발</option>
          <option value="3">문화여가</option>
          <option value="4">건강</option>
          <option value="5">언어</option>
          <option value="6">인문사회</option>
          <option value="7">자격증</option>
          <option value="8">경제</option>
        </select>

        <!-- 세부분류 -->
        <select id="subCategory" name="subCategory" class="form-select w-auto">
          <option value="">세부분류 선택</option>
        </select>

        <!-- 검색창 -->
        <input type="search" name="keyword" class="form-control w-25" placeholder="강좌명 검색" />
        <button type="submit" class="btn btn-warning px-4 py-2" style="white-space: nowrap;">검색</button>
      </form>
    </div>

    <!--  강좌 리스트 -->
    <div class="album py-5 bg-body-tertiary">
      <div class="container">
        <div class="row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3">

          <c:forEach var="lecture" items="${lectures}">
            <div class="col">
              <div class="card shadow-sm">
                <img src="/img/lecture/lecture_${lecture.lectureId}.jpg"
                     onerror="this.src='/img/common/no-image.png';"
                     class="card-img-top" height="225" alt="thumbnail" />
                <div class="card-body">
                  <p class="card-text">
                    <strong>${lecture.title}</strong><br>
                    강사: ${lecture.tutorName}<br>
                    가격: ${lecture.price}원
                  </p>
                  <a href="/lecture/content/${lecture.lectureId}" class="btn btn-sm btn-outline-primary">상세보기</a>
                </div>
              </div>
            </div>
          </c:forEach>

          <c:if test="${empty lectures}">
            <p class="text-center text-muted">등록된 강좌가 없습니다.</p>
          </c:if>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <nav aria-label="Page navigation">
      <ul class="pagination justify-content-center mt-4">
        <c:if test="${!lecturePage.first}">
          <li class="page-item"><a class="page-link" href="?page=${lecturePage.number - 1}&keyword=${keyword}">이전</a></li>
        </c:if>

        <c:forEach var="i" begin="0" end="${lecturePage.totalPages - 1}">
          <li class="page-item ${lecturePage.number == i ? 'active' : ''}">
            <a class="page-link" href="?page=${i}&keyword=${keyword}">${i + 1}</a>
          </li>
        </c:forEach>

        <c:if test="${!lecturePage.last}">
          <li class="page-item"><a class="page-link" href="?page=${lecturePage.number + 1}&keyword=${keyword}">다음</a></li>
        </c:if>
      </ul>
    </nav>
  </section>

  <!-- JS : 주제별 세부분류 자동 변경 -->
  <script>
    const subCategories = {
      1: ["프론트엔드", "백엔드", "데이터베이스", "인공지능(AI)", "클라우드/DevOps", "모바일 앱개발"],
      2: ["시간관리", "리더십", "생산성 향상", "자기소개서/면접"],
      3: ["사진/영상편집", "음악/작곡", "그림/디자인"],
      4: ["요리/베이킹", "피트니스", "요가/명상"],
      5: ["영어회화", "일본어", "중국어", "한국어"],
      6: ["심리학", "철학/역사", "정치/사회"],
      7: ["정보처리기사", "SQLD/ADsP", "토익/토플", "컴퓨터활용능력"],
      8: ["주식/투자", "부동산", "회계/재무관리"]
    };

    const mainSelect = document.getElementById("mainCategory");
    const subSelect = document.getElementById("subCategory");

    mainSelect.addEventListener("change", function () {
      const selected = this.value;
      subSelect.innerHTML = '<option value="">세부분류 선택</option>';

      if (subCategories[selected]) {
        subCategories[selected].forEach(sub => {
          const opt = document.createElement("option");
          opt.value = sub;
          opt.textContent = sub;
          subSelect.appendChild(opt);
        });
      }
    });
  </script>

</body>
</html>
