  <%@ page contentType="text/html;charset=UTF-8" language="java" %>

  <%@ include file="/WEB-INF/views/common/header.jsp" %>

  <section id="lecture">
    <!-- 왼쪽: 비디오 영역 -->
    <div class="video-area">
      <iframe src="https://www.youtube.com/embed/B-14Ksjonvk?si=JR4rw8rxElFyr3Vr" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
    </div>

    <!-- 오른쪽: 커리큘럼 -->
    <aside class="curriculum">
      <h3>강의 이름</h3>
      <h6>강사 이름</h6>
      <div class="progress-wrap">
        <div class="progress"><div class="bar" style="width: 83%;"></div></div>
        <p class="progress-text">
            진도율 <span>81</span> / 98 <span>(83%)</span>
        </p>
      </div>

      <!-- 아코디언 -->
      <div class="accordion" id="curriculumAccordion">
        <!-- 섹션1 -->
        <div class="accordion-item">
          <h2 class="accordion-header" id="headingOne">
            <button class="accordion-button" type="button" data-bs-toggle="collapse"
              data-bs-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
              섹션 1. 들어가며
            </button>
          </h2>
          <div id="collapseOne" class="accordion-collapse collapse show" aria-labelledby="headingOne" data-bs-parent="#curriculumAccordion">
            <div class="accordion-body">
              <ul class="list-unstyled mb-0">
                <li><a href="#">1. 강의 및 강사 소개 <span class="text-muted small">01:00</span></a></li>
                <li><a href="#">2. 수강생 커뮤니티 참가 안내 <span class="text-muted small">01:00</span></a></li>
                <li><a href="#">3. 예제 코드 및 강의 자료 안내 <span class="text-muted small">01:00</span></a></li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 섹션2 -->
        <div class="accordion-item">
          <h2 class="accordion-header" id="headingTwo">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
              data-bs-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
              섹션 2. JavaScript 기본
            </button>
          </h2>
          <div id="collapseTwo" class="accordion-collapse collapse" aria-labelledby="headingTwo" data-bs-parent="#curriculumAccordion">
            <div class="accordion-body">
              <ul class="list-unstyled mb-0">
                <li><a href="#">1. 변수와 상수<span class="text-muted small">01:00</span></a></li>
                <li><a href="#">2. 조건문 & 반복문<span class="text-muted small">01:00</span></a></li>
                <li><a href="#">3. 함수와 스코프<span class="text-muted small">01:00</span></a></li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 섹션3 -->
        <div class="accordion-item">
          <h2 class="accordion-header" id="headingThree">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
              data-bs-target="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
              섹션 3. JavaScript 심화
            </button>
          </h2>
          <div id="collapseThree" class="accordion-collapse collapse" aria-labelledby="headingThree" data-bs-parent="#curriculumAccordion">
            <div class="accordion-body">
              <ul class="list-unstyled mb-0">
                <li><a href="#">1. 클로저<span class="text-muted small">01:00</span></a></li>
                <li><a href="#">2. 프로토타입과 this<span class="text-muted small">01:00</span></a></li>
                <li><a href="#">3. ES6 클래스<span class="text-muted small">01:00</span></a></li>
              </ul>
            </div>
          </div>
        </div>

      </div>
    </aside>
  </section>