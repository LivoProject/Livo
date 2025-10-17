// /static/js/register.js
(function () {
    const $ = (sel) => document.querySelector(sel);

    const cfg = (window.__REG_CFG__) || {};
    const URL_REGISTER   = cfg.urlRegister;
    const URL_VAL_EMAIL  = cfg.urlValEmail;
    const URL_VAL_NICK   = cfg.urlValNick;
    const URL_VAL_PW     = cfg.urlValPw;
    const URL_SEND_CODE  = cfg.urlSendCode;
    const URL_VERIFY_CODE= cfg.urlVerifyCode;

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

    // ───────────────── helpers ─────────────────
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
        throw new Error('JSON이 아닌 응답: ' + text.slice(0, 200));
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

    // ───────────── 초기 상태(이미 인증된 경우) ─────────────
    if (verifiedEmailMeta) {
        emailInput.value = verifiedEmailMeta;
        emailInput.readOnly = true;
        emailInput.classList.add('is-valid');
        setInfo(verifyInfo, '이메일 인증 완료', 'text-success');
        codeBlock.style.display = 'none';
        disable(btnSendCode, true);
    }

    // ───────────── 필드 즉시검증 ─────────────
    [
        {name: 'email',    base: URL_VAL_EMAIL},
        {name: 'password', base: URL_VAL_PW},
        {name: 'nickname', base: URL_VAL_NICK},
    ].forEach(({name, base}) => {
        const input = document.querySelector(`[name="${name}"]`);
        if (!input || !base) return;

        let timer;
        const run = async () => {
            try {
                const q = encodeURIComponent(input.value || '');
                const res = await fetch(`${base}?value=${q}`, {
                    headers: { [csrfH]: csrf },
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

        input.addEventListener('input', () => {
            clearTimeout(timer);
            timer = setTimeout(run, 300);
        });
        input.addEventListener('blur', run);
    });

    // ───────────── 인증코드 전송 ─────────────
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
                    [csrfH]: csrf
                },
                credentials: 'same-origin',
                body: new URLSearchParams({ email }).toString()
            });

            const data = await safeJson(res);
            if (res.ok && (data.ok || data.success)) {
                setInfo(verifyInfo, data.message || '인증코드를 전송했습니다. 이메일을 확인하세요.', 'text-success');
                codeBlock.style.display = '';                 // 코드 입력 보이기
                startCountdown(data.cooldownRemainSec || 60); // 서버에서 남은 쿨다운 내려주면 사용
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

    // ───────────── 코드 확인 ─────────────
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
                    [csrfH]: csrf
                },
                credentials: 'same-origin',
                body: new URLSearchParams({ email, code }).toString()
            });
            const data = await safeJson(res);

            if (res.ok && (data.ok || data.success)) {
                // ✅ 서버가 세션 VERIFIED_EMAIL 세팅 완료
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

    // ───────────── 폼 제출(JSON) ─────────────
    form?.addEventListener('submit', async (e) => {
        e.preventDefault();

        // 서버 세션의 VERIFIED_EMAIL이 현재 입력값과 같아야 진행
        const verified = (document.querySelector('meta[name="verifiedEmail"]')?.content || '').trim().toLowerCase();
        const current  = (emailInput.value || '').trim().toLowerCase();
        if (!verified || verified !== current) {
            setMsg('email', false, '이메일 인증 후 진행해 주세요.');
            return;
        }

        // 필드 메시지 초기화
        document.querySelectorAll('[data-msg]').forEach(el => { el.textContent = ''; el.className = ''; });

        try {
            const fd = new FormData(form);
            const payload = Object.fromEntries(fd.entries());

            const res = await fetch(URL_REGISTER, {
                method: 'POST',
                credentials: 'same-origin',
                headers: { 'Content-Type': 'application/json; charset=UTF-8', [csrfH]: csrf },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const data = await safeJson(res).catch(() => ({}));
                const entries = Object.entries(data || {});
                if (entries.length === 0) { alert('입력값을 다시 확인해주세요.'); return; }
                entries.forEach(([k, v]) => setMsg(k, false, String(v)));
                return;
            }

            const data = await safeJson(res).catch(() => null);
            if (data?.success === true) {
                alert('회원가입이 완료되었습니다.');
                window.location.href = '/auth/login';
            } else {
                alert('요청을 처리할 수 없습니다.');
            }
        } catch (err) {
            console.error('submit error:', err);
            alert('네트워크 또는 서버 오류가 발생했습니다.');
        }
    });

    // 비밀번호 보기 토글(기존 UI 유지)
    document.querySelector('.password-toggle')?.addEventListener('click', () => {
        const pw = $('#password');
        if (!pw) return;
        pw.type = pw.type === 'password' ? 'text' : 'password';
    });
})();
