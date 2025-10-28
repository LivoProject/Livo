// /static/js/register.js
(function () {
    const $ = (sel) => document.querySelector(sel);

    const cfg = (window.__REG_CFG__) || {};
    const URL_REGISTER    = cfg.urlRegister;
    const URL_VAL_EMAIL   = cfg.urlValEmail;
    const URL_VAL_NICK    = cfg.urlValNick;
    const URL_VAL_PW      = cfg.urlValPw;
    const URL_SEND_CODE   = cfg.urlSendCode;
    const URL_VERIFY_CODE = cfg.urlVerifyCode;
    const URL_LOGIN       = cfg.urlLogin;
    const URL_VAL_PHONE   = cfg.urlValPhone;


    const csrf  = document.querySelector('meta[name="_csrf"]')?.content || '';
    const csrfH = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';

    const verifiedEmailMeta = (document.querySelector('meta[name="verifiedEmail"]')?.content || '').trim();

    const emailInput  = $('#email');
    const verifyInfo  = $('#verifyInfo');
    const btnSendCode = $('#btnSendCode');
    const countdownEl = $('#sendCountdown');

    const codeBlock   = $('#codeBlock');
    const codeInput   = $('#code');
    const btnVerify   = $('#btnVerifyCode');
    const codeMsg     = $('#codeMsg');

    const form        = $('#signupForm');

    // helpers
    function normalizePhone(v = '') {
        let s = String(v).replace(/[^\d+]/g, ''); // 숫자/+만 남김
        if (s.startsWith('+82')) s = '0' + s.slice(3); // +82 → 0
        return s;
    }
    function setMsg(name, ok, text = '') {
        const el = document.querySelector(`[data-msg="${name}"]`);
        if (!el) return;
        el.className = ok ? 'valid' : 'error';
        el.textContent = ok ? '' : text;
    }
    function setInfo(el, text, klass) {
        if (!el) return;
        el.className = klass || '';
        el.textContent = text || '';
    }
    function disable(el, flag) { if (el) el.disabled = !!flag; }

    async function safeJson(res) {
        const ct = (res.headers.get('content-type') || '').toLowerCase();
        if (ct.includes('application/json')) return await res.json();
        const text = await res.text();
        throw new Error('JSON이 아닌 응답: ' + text.slice(0, 300));
    }

    function startCountdown(sec) {
        if (!sec || sec <= 0) return;
        countdownEl.style.display = '';
        let remain = sec;
        countdownEl.textContent = `재전송 대기 ${remain}s`;
        const t = setInterval(() => {
            remain -= 1;
            if (remain <= 0) {
                clearInterval(t);
                countdownEl.style.display = 'none';
                disable(btnSendCode, false);
                btnSendCode.textContent = '인증코드 전송';
            } else {
                countdownEl.textContent = `재전송 대기 ${remain}s`;
            }
        }, 1000);
    }

    // 초기 상태(이미 인증된 경우)
    if (verifiedEmailMeta) {
        emailInput.value = verifiedEmailMeta;
        emailInput.readOnly = true;
        emailInput.classList.add('is-valid');
        setInfo(verifyInfo, '이메일 인증 완료', 'text-success');
        codeBlock.style.display = 'none';
        disable(btnSendCode, true);
    }

    // 필드 즉시검증
    [
        {name: 'email',    base: URL_VAL_EMAIL},
        {name: 'password', base: URL_VAL_PW},
        {name: 'nickname', base: URL_VAL_NICK},
        {name: 'phone',    base: URL_VAL_PHONE},
    ].forEach(({name, base}) => {
        const input = document.querySelector(`[name="${name}"]`);
        if (!input || !base) return;

        let timer;
        const run = async () => {
            try {
                const q = encodeURIComponent(input.value || '');
                const res = await fetch(`${base}?value=${q}`, {
                    headers: { [csrfH]: csrf, 'Accept': 'application/json' },
                    credentials: 'same-origin'
                });
                if (!res.ok) throw new Error(`검증 실패(${res.status})`);
                const data = await safeJson(res);
                setMsg(name, data.valid, data.message || '');
            } catch (err) {
                console.error('validate error:', err);
                setMsg(name, false, '검증 요청 중 오류가 발생했습니다.');
            }
        };
        input.addEventListener('input', () => { clearTimeout(timer); timer = setTimeout(run, 300); });
        input.addEventListener('blur', run);
    });

    // 인증코드 전송
    btnSendCode?.addEventListener('click', async () => {
        setInfo(verifyInfo, '');
        const email = (emailInput.value || '').trim();
        if (!email) { setMsg('email', false, '이메일을 입력해 주세요.'); emailInput.focus(); return; }

        disable(btnSendCode, true);
        btnSendCode.textContent = '전송 중...';

        try {
            const res = await fetch(URL_SEND_CODE, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    [csrfH]: csrf,
                    'Accept': 'application/json'
                },
                credentials: 'same-origin',
                body: new URLSearchParams({ email }).toString()
            });
            const data = await safeJson(res);

            if (res.ok && (data.ok || data.success)) {
                setInfo(verifyInfo, data.message || '인증코드를 전송했습니다. 이메일을 확인하세요.', 'text-success');
                codeBlock.style.display = '';
                startCountdown(data.cooldownRemainSec || 60);
            } else {
                setInfo(verifyInfo, data.message || '코드 전송에 실패했습니다.', 'text-danger');
                if (data.cooldownRemainSec) startCountdown(data.cooldownRemainSec);
                else { disable(btnSendCode, false); btnSendCode.textContent = '인증코드 전송'; }
            }
        } catch (e) {
            console.error(e);
            setInfo(verifyInfo, '네트워크 오류가 발생했습니다.', 'text-danger');
            disable(btnSendCode, false);
            btnSendCode.textContent = '인증코드 전송';
        }
    });

    // 코드 확인
    btnVerify?.addEventListener('click', async () => {
        setInfo(codeMsg, '');
        const email = (emailInput.value || '').trim();
        const code  = (codeInput.value  || '').trim();

        if (!email) { setMsg('email', false, '이메일을 입력해 주세요.'); emailInput.focus(); return; }
        if (!code || code.length !== 6) { setInfo(codeMsg, '6자리 코드를 입력해 주세요.', 'text-danger'); codeInput.focus(); return; }

        disable(btnVerify, true);
        btnVerify.textContent = '확인 중...';

        try {
            const res = await fetch(URL_VERIFY_CODE, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                    [csrfH]: csrf,
                    'Accept': 'application/json'
                },
                credentials: 'same-origin',
                body: new URLSearchParams({ email, code }).toString()
            });
            const data = await safeJson(res);

            if (res.ok && (data.ok || data.success)) {
                // meta에 인증 이메일 반영 → 제출 검사 통과
                const meta = document.querySelector('meta[name="verifiedEmail"]');
                if (meta) meta.setAttribute('content', email.toLowerCase());

                setInfo(codeMsg, data.message || '인증이 완료되었습니다.', 'text-success');
                emailInput.readOnly = true;
                emailInput.classList.add('is-valid');
                codeBlock.style.display = 'none';
                disable(btnSendCode, true);
                setInfo(verifyInfo, '이메일 인증 완료', 'text-success');
            } else {
                setInfo(codeMsg, data.message || '코드가 올바르지 않거나 만료되었습니다.', 'text-danger');
            }
        } catch (e) {
            console.error(e);
            setInfo(codeMsg, '네트워크 오류가 발생했습니다.', 'text-danger');
        } finally {
            disable(btnVerify, false);
            btnVerify.textContent = '코드 확인';
        }
    });

    // 폼 제출(JSON)
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 세션 VERIFIED_EMAIL == 현재 이메일?
        const verified = (document.querySelector('meta[name="verifiedEmail"]')?.content || '').trim().toLowerCase();
        const current  = (emailInput.value || '').trim().toLowerCase();
        if (!verified || verified !== current) {
            setMsg('email', false, '이메일 인증 후 진행해 주세요.');
            return;
        }

        // 메시지 초기화
        document.querySelectorAll('[data-msg]').forEach(el => { el.textContent = ''; el.className = ''; });

        try {
            const fd = new FormData(form);
            const payload = Object.fromEntries(fd.entries());

            payload.phone = normalizePhone(payload.phone);

            //서버 전송
            const res = await fetch(URL_REGISTER, {
                method: 'POST',
                credentials: 'same-origin',
                // JSON 요청이라는 의사 표시 강화 + CORS/협상 문제 회피
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8',
                    'Accept': 'application/json',
                    [csrfH]: csrf,
                    // 선택) XHR 힌트 — 백엔드에서 필요 시 Ajax 판단용으로도 씀
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify(payload),
                // 선택) 리다이렉트가 오면 수동 처리
                redirect: 'follow'
            });

            // 리다이렉트/HTML 응답 방어 로직
            // ... fetch 후
            const ct = (res.headers.get('content-type') || '').toLowerCase();
            if (ct.includes('application/json')) {
                const data = await res.json();

                // 성공
                if (data && (data.success === true || data.ok === true)) {
                    alert('회원가입이 완료되었습니다.');
                    window.location.href = URL_LOGIN;
                    return;
                }

                //  공통 에러 메시지 키가 있을 때는 알럿으로 보여주기
                if (typeof data.error === 'string' && data.error.trim()) {
                    alert(data.error);
                    return;
                }

                //  필드 에러 매핑
                const entries = Object.entries(data || {});
                if (entries.length) {
                    entries.forEach(([k, v]) => {
                        if (k === 'error') {
                            alert(String(v));
                        } else {
                            setMsg(k, false, String(v));
                        }
                    });
                    return;
                }

                alert('입력값을 다시 확인해주세요.');
                return;
            }

// HTML이면 성공으로 간주
            if (res.redirected && res.url) {
                window.location.href = res.url;
                return;
            }
            if (ct.includes('text/html')) {
                alert('회원가입이 완료되었습니다.');
                window.location.href = URL_LOGIN;
                return;
            }

            alert('요청을 처리할 수 없습니다.');

        } catch (err) {
            console.error('submit error:', err);
            alert('네트워크 또는 서버 오류가 발생했습니다.');
        }
    });

    // 비밀번호 보기 토글
    //document.querySelector('.password-toggle')?.addEventListener('click', () => {
    //    const pw = $('#password');
    //    if (!pw) return;
    //    pw.type = pw.type === 'password' ? 'text' : 'password';
    //});
    // 비밀번호 보기 토글 (각 버튼마다 독립 작동)
    document.querySelectorAll(".password-toggle").forEach(button => {
        button.addEventListener("click", () => {
            const input = button.parentElement.querySelector(".pw-input");
            const icon = button.querySelector("i");
            const isPassword = input.getAttribute("type") === "password";

            input.setAttribute("type", isPassword ? "text" : "password");
            icon.classList.toggle("bi-eye");
            icon.classList.toggle("bi-eye-slash");
        });
    });
})();
