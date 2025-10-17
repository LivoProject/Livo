// /static/js/mypage-info.js
document.addEventListener("DOMContentLoaded", function () {
    const $ = (sel) => document.querySelector(sel);

    const csrf  = document.querySelector('meta[name="_csrf"]')?.content || '';
    const csrfH = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';
    const form  = $('#signupForm');
    const btnSubmit = form?.querySelector('.btn-submit');

    // ────────────────────── helper ──────────────────────
    function setMsg(name, ok, text = '') {
        const el = document.querySelector(`[data-msg="${name}"]`);
        if (!el) return;
        el.className = ok ? 'valid' : 'error';
        el.textContent = ok ? '' : text;
        fieldValid[name] = ok;
        toggleSubmitButton();
    }

    async function safeJson(res) {
        const ct = (res.headers.get('content-type') || '').toLowerCase();
        if (ct.includes('application/json')) return await res.json();
        const text = await res.text();
        throw new Error('JSON이 아닌 응답: ' + text.slice(0, 200));
    }

    function toggleSubmitButton() {
        const allValid = Object.values(fieldValid).every((v) => v === true);
        if (btnSubmit) btnSubmit.disabled = !allValid;
    }

    // ────────────────────── 유효성 검사 대상 ──────────────────────
    const nicknameInput = $('#nickname');
    const phoneInput    = $('#phone');
    const nameInput     = $('#username');
    const birthInput    = $('#birth');

    // 초기 상태
    const fieldValid = {
        nickname: true,   // 기본값 true (수정 안 하면 그대로 통과)
        phone: true,
        username: true,
        birth: true
    };

    toggleSubmitButton(); // 처음엔 유효성 따라 버튼 상태 반영

    // ───────────── 닉네임 중복검사 + 스피너 ─────────────
    nicknameInput?.addEventListener('input', async () => {
        const value = nicknameInput.value.trim();
        const msgEl = document.querySelector('[data-msg="nickname"]');

        if (value.length < 2) {
            setMsg('nickname', false, '2자 이상 입력해주세요.');
            return;
        }

        msgEl.innerHTML = '<span class="spinner-border spinner-border-sm text-primary" role="status" aria-hidden="true"></span> 확인 중...';
        fieldValid.nickname = false;
        toggleSubmitButton();

        try {
            const res = await fetch(`/auth/validate/nickname?value=${encodeURIComponent(value)}`, {
                headers: { [csrfH]: csrf },
                credentials: 'same-origin'
            });
            const data = await safeJson(res);
            setMsg('nickname', data.valid, data.message || '');
        } catch (err) {
            console.error(err);
            setMsg('nickname', false, '검증 중 오류가 발생했습니다.');
        }
    });

    // ───────────── 전화번호 자동 하이픈 + 형식검사 ─────────────
    phoneInput?.addEventListener('input', (e) => {
        let value = e.target.value.replace(/[^0-9]/g, '');
        if (value.length > 3 && value.length <= 7) {
            value = value.replace(/(\d{3})(\d+)/, '$1-$2');
        } else if (value.length > 7) {
            value = value.replace(/(\d{3})(\d{4})(\d{1,4})/, '$1-$2-$3');
        }
        e.target.value = value;

        const pattern = /^010-\d{4}-\d{4}$/;
        if (!pattern.test(value)) {
            setMsg('phone', false, '형식: 010-1234-5678');
        } else {
            setMsg('phone', true, '');
        }
    });

    // ───────────── 이름 형식검사 (한글 2자 이상) ─────────────
    nameInput?.addEventListener('input', () => {
        const value = nameInput.value.trim();
        const pattern = /^[가-힣]{2,}$/;
        if (!pattern.test(value)) {
            setMsg('username', false, '한글 2자 이상 입력해주세요.');
        } else {
            setMsg('username', true, '');
        }
    });

    // ───────────── 생년월일 검사 ─────────────
    birthInput?.addEventListener('blur', () => {
        if (!birthInput.value) setMsg('birth', false, '생년월일을 입력해주세요.');
        else setMsg('birth', true, '');
    });

    // ───────────── 폼 제출 전 재검증 ─────────────
    form?.addEventListener('submit', (e) => {
        const allValid = Object.values(fieldValid).every((v) => v === true);
        if (!allValid) {
            e.preventDefault();
            alert('입력값을 다시 확인해주세요.');
        }
    });
});
