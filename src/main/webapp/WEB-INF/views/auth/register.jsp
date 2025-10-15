<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="ko"><head>
  <meta charset="UTF-8"/>
  <title>회원가입</title>
  <meta name="_csrf" content="${_csrf.token}"/>
  <meta name="_csrf_header" content="${_csrf.headerName}"/>

  <c:url var="urlRegister" value="/auth/register"/>
  <c:url var="urlValEmail" value="/auth/validate/email"/>
  <c:url var="urlValNick"  value="/auth/validate/nickname"/>
  <c:url var="urlValPw"    value="/auth/validate/password"/>

    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/reset.css">
    <link rel="stylesheet" href="/css/form.css">
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
    />



</head>

<body id="register">
<div class="register-container">
<h2>회원가입</h2>

<form:form id="signupForm" method="post" modelAttribute="signUpRequest">
    <!-- 이메일 -->
  <div class="field">
    <label class="form-label">이메일</label>
    <form:input path="email"  placeholder="name@example.com" cssClass="form-control"/>
    <span data-msg="email"></span>
  </div>

    <!-- 비밀번호 -->
  <div class="field">
    <label  class="form-label">비밀번호</label><br/>
      <div class="position-relative">
    <form:password path="password"  cssClass="form-control" placeholder="영문·숫자·특수문자 8~20자"/>
    <span data-msg="password"></span>
          <button class="password-toggle" type="button" aria-label="비밀번호 표시 토글">보기</button>
      </div>
  </div>

    <!-- 닉네임 -->
  <div class="field">
    <label class="form-label">닉네임</label><br/>
    <form:input path="nickname"  cssClass="form-control" placeholder="홍이"/>
    <span data-msg="nickname"></span>
  </div>


    <!-- 이름 -->
  <div class="field">
    <label class="form-label">이름</label><br/>
    <form:input path="name" cssClass="form-control" placeholder="홍길동"/>
  </div>

    <!-- 전화 -->
  <div class="field">
    <label class="form-label">전화</label><br/>
    <form:input path="phone"  cssClass="form-control" placeholder="010-1234-5678"/>
  </div>

    <!-- 생년월일 -->
  <div class="field">
    <label class="form-label">생년월일</label><br/>
    <form:input path="birth" placeholder="yyyy-mm-dd"  cssClass="form-control"/>
  </div>

    <!-- 성별 -->
  <div class="field">
    <label class="form-label">성별</label><br/>
      <div class="gender-group">
          <label class="gender-radio">
              <input type="radio" name="gender" value="M"/> <span>남성</span>
          </label>
          <label class="gender-radio">
              <input type="radio" name="gender" value="F"/> <span>여성</span>
          </label>
      </div>
<%--    <form:input path="gender"/>--%>
  </div>

    <div class="field">
  <button type="submit" class="btn-submit">가입하기</button>
    </div>
</form:form>


    <p>이미 회원이신가요? <a href="<c:url value='/auth/login'/>">로그인</a></p>
</div>
</body>
</html>
<script>
(function(){
  const csrf  = document.querySelector('meta[name="_csrf"]')?.content || '';
  const csrfH = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';

  const URL_REGISTER  = "${urlRegister}";
  const URL_VAL_EMAIL = "${urlValEmail}";
  const URL_VAL_NICK  = "${urlValNick}";
  const URL_VAL_PW    = "${urlValPw}";

  function setMsg(name, ok, text=''){
    const el=document.querySelector(`[data-msg="${name}"]`);
    if(!el) return;
    el.className= ok?'valid':'error';
    el.textContent= ok?'':text;
  }
  function setGlobal(msg){
    let box=document.getElementById('globalError');
    if(!box){
      box=document.createElement('div');
      box.id='globalError';
      box.className='error';
      document.body.prepend(box);
    }
    box.textContent = msg || '처리 중 오류가 발생했습니다.';
  }

  async function safeJson(res){
    const ct = (res.headers.get('content-type')||'').toLowerCase();
    if (ct.includes('application/json')) return await res.json();
    const text = await res.text();
    throw new Error('JSON이 아닌 응답: ' + text.slice(0,120));
  }

  // 필드 즉시검증
  [
    {name:'email',    base:URL_VAL_EMAIL},
    {name:'password', base:URL_VAL_PW},
    {name:'nickname', base:URL_VAL_NICK},
  ].forEach(({name, base})=>{
    const input=document.querySelector(`[name="${name}"]`);
    if(!input) return;
    let t;
    const run=async()=>{
      try{
        const q = encodeURIComponent(input.value || '');
        const res = await fetch(`${base}?value=${q}`, { headers: { [csrfH]: csrf }, credentials:'same-origin' });
        if(!res.ok) throw new Error(`검증 실패(${res.status})`);
        const data = await safeJson(res);
        setMsg(name, data.valid, data.message || '');
      }catch(err){
        setMsg(name, false, '검증 요청 중 오류가 발생했습니다.');
        console.error('validate error:', err);
      }
    };
    input.addEventListener('input', ()=>{ clearTimeout(t); t=setTimeout(run,300); });
    input.addEventListener('blur', run);
  });

  // 폼 전체 AJAX 제출
  document.getElementById('signupForm').addEventListener('submit', async (e)=>{
    e.preventDefault();
    document.querySelectorAll('[data-msg]').forEach(el=>{el.textContent=''; el.className='';});

    try{
      const fd = new FormData(e.target);
      const payload = Object.fromEntries(fd.entries());

      const res = await fetch(URL_REGISTER, {
        method:'POST',
        credentials: 'same-origin', // ✅ 쿠키 포함 (CSRF 토큰 검증용)
        headers:{ 'Content-Type':'application/json; charset=UTF-8', [csrfH]: csrf },
        body: JSON.stringify(payload)
      });

      if(res.ok){
        alert('회원가입 완료');
        location.href='${pageContext.request.contextPath}/auth/login';
        return;
      }

      const data = await safeJson(res);
      if (data && typeof data === 'object'){
        const entries = Object.entries(data);
        if(entries.length === 0){ setGlobal('입력값을 다시 확인해주세요.'); return; }
        entries.forEach(([k,v])=> setMsg(k,false,String(v)));
      } else {
        setGlobal('처리 중 오류가 발생했습니다.');
      }
    }catch(err){
      setGlobal('네트워크 또는 서버 오류가 발생했습니다.');
      console.error('submit error:', err);
    }
  });
})();
</script>



</body></html>
