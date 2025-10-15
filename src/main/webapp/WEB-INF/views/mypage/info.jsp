<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section id="mypage" class="container">

  <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

  <main class="main-content">
    <h3>내 정보</h3>

    <!-- 플래시/전역 메시지 -->
    <c:if test="${not empty msg}">
      <div class="alert alert-success">${msg}</div>
    </c:if>
    <c:if test="${not empty error}">
      <div class="alert alert-danger">${error}</div>
    </c:if>

    <!-- 기본 정보 -->
    <div class="card mb-24">
      <div class="card-body">
        <p class="mb-0"><strong>이메일</strong> : ${email}</p>
      </div>
    </div>

    <!-- ===================== 닉네임 변경 ===================== -->
    <div class="card mb-24">
      <div class="card-header"><h4 class="mb-0">닉네임 변경</h4></div>
      <div class="card-body">
        <!-- 반드시 modelAttribute="profileForm" -->
        <form:form method="post"
                   modelAttribute="profileForm"
                   action="${pageContext.request.contextPath}/mypage/info/nickname">

          <!-- CSRF -->
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

          <div class="field">
            <label for="nickname" class="form-label">새 닉네임</label>
            <div class="d-flex gap-8" style="display:flex; gap:8px; align-items:center;">
              <!-- 여기가 form:form 안이어야 path가 인식됩니다 -->
              <form:input path="nickname" id="nickname" cssClass="form-control" placeholder="예: 물살잡이"/>
              <button type="button" id="btnCheckNickname" class="btn btn-secondary">중복확인</button>
            </div>
            <div id="nickCheckMsg" class="small" style="margin-top:6px;"></div>
            <form:errors path="nickname" cssClass="text-danger small"/>
          </div>

          <div class="field">
            <button type="submit" class="btn-submit">닉네임 변경</button>
          </div>
        </form:form>
      </div>
    </div>

    <!-- ===================== 비밀번호 변경 ===================== -->
    <div class="card">
      <div class="card-header"><h4 class="mb-0">비밀번호 변경</h4></div>
      <div class="card-body">
        <!-- 반드시 modelAttribute="passwordForm" -->
        <form:form method="post"
                   modelAttribute="passwordForm"
                   action="${pageContext.request.contextPath}/mypage/info/password">

          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

          <div class="field">
            <label for="currentPassword" class="form-label">현재 비밀번호</label>
            <form:password path="currentPassword" id="currentPassword" cssClass="form-control"/>
            <form:errors path="currentPassword" cssClass="text-danger small"/>
          </div>

          <div class="field">
            <label for="newPassword" class="form-label">새 비밀번호</label>
            <div class="position-relative">
              <form:password path="newPassword" id="newPassword" cssClass="form-control"
                             placeholder="영문·숫자·특수문자 8~64자"/>
              <button class="password-toggle" type="button" aria-label="비밀번호 표시 토글">보기</button>
            </div>
            <form:errors path="newPassword" cssClass="text-danger small"/>
          </div>

          <div class="field">
            <button type="submit" class="btn-submit">비밀번호 변경</button>
          </div>
        </form:form>
      </div>
    </div>
  </main>
</section>

<script>
  // 비밀번호 보기 토글
  (function() {
    const btn = document.querySelector('.password-toggle');
    const input = document.getElementById('newPassword');
    if (!btn || !input) return;
    btn.addEventListener('click', () => {
      input.type = input.type === 'password' ? 'text' : 'password';
      btn.textContent = input.type === 'password' ? '보기' : '숨기기';
    });
  })();

  // CSRF 쿠키에서 토큰 읽기
  function getCsrfToken() {
    const m = document.cookie.match(/(?:^|;\\s*)XSRF-TOKEN=([^;]+)/);
    return m ? decodeURIComponent(m[1]) : null;
  }

  // 닉네임 중복확인
  (function () {
    const btn   = document.getElementById('btnCheckNickname');
    const input = document.getElementById('nickname');
    const msg   = document.getElementById('nickCheckMsg');
    if (!btn || !input || !msg) return;

    function show(ok, text) {
      msg.textContent = text;
      msg.classList.remove('text-danger','text-success');
      msg.classList.add(ok ? 'text-success' : 'text-danger');
    }

    btn.addEventListener('click', async () => {
      const nickname = (input.value || '').trim();
      if (nickname.length < 2 || nickname.length > 40) {
        show(false, '닉네임은 2~40자여야 합니다.');
        return;
      }
      btn.disabled = true;
      show(true, '확인 중...');

      try {
        const qs = new URLSearchParams({ nickname }).toString();
        const res = await fetch(`${window.location.origin}/auth/validate/nickname?` + qs, {
          method: 'GET',
          headers: { 'X-CSRF-TOKEN': getCsrfToken() || '' }
        });
        if (!res.ok) throw new Error('요청 실패');
        const data = await res.json();
        const exists = (typeof data.exists === 'boolean') ? data.exists
                      : (typeof data.available === 'boolean') ? !data.available
                      : !!data.taken;
        if (exists) show(false, '이미 사용 중인 닉네임입니다.');
        else        show(true,  '사용 가능한 닉네임입니다.');
      } catch (e) {
        show(false, '중복확인 중 오류가 발생했습니다.');
      } finally {
        btn.disabled = false;
      }
    });

    input.addEventListener('input', () => { msg.textContent = ''; msg.className = 'small'; });
  })();
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
