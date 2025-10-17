<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

  <!-- main.css 스타일 적용용 wrapper -->
  <main id="main">

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
    <div id="recommend">
    <div class="container">
      <h3>전체 강좌</h3>
      <div class="recommend-grid">

        <c:forEach var="lecture" items="${lectures}">
          <a href="/lecture/content/${lecture.lectureId}" class="card popular-card">

            <!-- 썸네일 -->
            <div class="card-thumb" style="
                  background-image: url('/img/lecture/lecture_${lecture.lectureId}.jpg');
                  background-size: cover; background-position: center;
                  height: 200px; border-radius: 12px 12px 0 0;">
            </div>

            <!-- 강좌정보 -->
            <div class="card-body">
            <h6>${lecture.title}</h6>
            <p>${lecture.tutorName}∣<fmt:formatNumber value="${lecture.price}" type="number"/></p>
               <div class="card-review">
                    <div>
                        <span>⭐4.8</span>
                        <span>(22)</span>
                    </div>
                    <div>
                        <i class="bi bi-person-fill"></i>
                        <span>${lecture.reservationCount}</span>
                    </div>
              </div>
          </div>
          </a>
        </c:forEach>

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


  </main>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>