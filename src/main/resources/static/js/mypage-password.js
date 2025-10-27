document.addEventListener("DOMContentLoaded", () => {
    const pwInput = document.getElementById("newPassword");
    const confirmInput = document.getElementById("confirmPassword");
    const pwMsg = document.getElementById("pwMsg");
    const confirmMsg = document.getElementById("confirmMsg");
    const submitBtn = document.getElementById("submitBtn");

    // 비밀번호 규칙 (8~20자, 영문+숫자+특수문자)
    function validatePassword(pw) {
        const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;
        return regex.test(pw);
    }

    // 유효성 검사 및 버튼 제어
    function checkForm() {
        const pw = pwInput.value.trim();
        const cf = confirmInput.value.trim();

        let valid = true;

        if (!validatePassword(pw)) {
            pwMsg.textContent = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.";
            pwMsg.className = "error";
            valid = false;
        } else {
            pwMsg.textContent = "사용 가능한 비밀번호입니다.";
            pwMsg.className = "valid";
        }

        if (cf === "" || cf !== pw) {
            confirmMsg.textContent = "비밀번호가 일치하지 않습니다.";
            confirmMsg.className = "error";
            valid = false;
        } else {
            confirmMsg.textContent = "비밀번호가 일치합니다.";
            confirmMsg.className = "valid";
        }

        submitBtn.disabled = !valid;
    }

    // 비밀번호 입력 감지
    const pwInputs = document.querySelectorAll(".pw-input");
    pwInputs.forEach(input => {
        input.addEventListener("input", checkForm);
    });

    // 비밀번호 보기 토글 (각 버튼마다 독립 작동)
    document.querySelectorAll(".toggle-password").forEach(button => {
        button.addEventListener("click", () => {
            const input = button.parentElement.querySelector(".pw-input");
            const icon = button.querySelector("i");
            const isPassword = input.getAttribute("type") === "password";

            input.setAttribute("type", isPassword ? "text" : "password");
            icon.classList.toggle("bi-eye");
            icon.classList.toggle("bi-eye-slash");
        });
    });
});


