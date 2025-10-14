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

  <style>
    .error{color:#d32f2f;display:block;margin-top:4px}
    .valid{color:#2e7d32;display:block;margin-top:4px}
    .field{margin:12px 0}
  </style>
</head>
<body>
<h2>회원가입</h2>

<form:form id="signupForm" method="post" modelAttribute="signUpRequest">
  <div class="field">
    <label>Email</label><br/>
    <form:input path="email"/>
    <span data-msg="email"></span>
  </div>

  <div class="field">
    <label>Password</label><br/>
    <form:password path="password"/>
    <small>대/소문자·숫자·특수문자 포함 8~64자</small><br/>
    <span data-msg="password"></span>
  </div>

  <div class="field">
    <label>닉네임</label><br/>
    <form:input path="nickname"/>
    <span data-msg="nickname"></span>
  </div>

  <div class="field">
    <label>이름</label><br/>
    <form:input path="name"/>
  </div>

  <div class="field">
    <label>전화</label><br/>
    <form:input path="phone"/>
  </div>

  <div class="field">
    <label>생년월일 (yyyy-MM-dd)</label><br/>
    <form:input path="birth"/>
  </div>

  <div class="field">
    <label>성별 (M/F)</label><br/>
    <form:input path="gender"/>
  </div>

  <button type="submit">가입</button>
</form:form>

<p><a href="<c:url value='/auth/login'/>">로그인</a></p>

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
