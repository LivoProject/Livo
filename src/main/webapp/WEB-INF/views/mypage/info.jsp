<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<!-- 컨텐츠 -->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 컨텐츠 -->
    <main class="main-content">
    <h3>내 정보</h3>

    <form id="signupForm" novalidate>
        <!-- 이메일 -->
        <div class="field">
        <label for="email" class="form-label"><span class="required-mark">*</span>이메일</label>
        <input
            type="email"
            class="form-control"
            id="email"
            placeholder="name@example.com"
            readonly
            required
            value="${mypage.email}"
        />
        </div>

        <!-- 비밀번호 -->
        <div class="field">
            <label for="password" class="form-label"><span class="required-mark">*</span>비밀번호</label>
            <div class="position-relative">
                <input
                    type="password"
                    class="form-control"
                    id="password"
                    placeholder="영문·숫자·특수문자 8~20자"
                    required
                />
                <button
                    class="password-toggle"
                    type="button"
                    aria-label="비밀번호 표시 토글"
                >
                보기
                </button>
            </div>
        </div>

        <!-- 닉네임 -->
        <div class="field">
            <label for="nickname" class="form-label"><span class="required-mark">*</span>닉네임</label>
            <input
                type="text"
                class="form-control"
                id="nickname"
                required
                value="${mypage.nickname}"
            />
        </div>

        <!-- 이름 -->
        <div class="field">
        <label for="name" class="form-label"><span class="required-mark">*</span>이름</label>
        <input
            type="text"
            class="form-control"
            id="name"
            placeholder="홍길동"
            required
            value="${mypage.username}"
        />
        </div>

        <!-- 전화번호 -->
        <div class="field">
        <label for="phone" class="form-label">전화번호</label>
        <input
            type="tel"
            class="form-control"
            id="phone"
            required
            value="${mypage.phone}"
        />
        </div>

        <!-- 생년월일 -->
        <div class="field">
        <label for="birth" class="form-label">생년월일</label>
        <input type="date" class="form-control" id="birth" required value="${mypage.birth}" />
        </div>

        <!-- 성별 -->
        <div class="field">
            <label class="form-label d-block">성별</label>
            <div class="gender-group">
                <label class="gender-radio">
                    <input type="radio" name="gender" value="M" required />
                    <span>남성</span>
                </label>
                <label class="gender-radio">
                    <input type="radio" name="gender" value="F" required />
                    <span>여성</span>
                </label>
            </div>
        </div>

        <!-- 버튼 -->
        <div class="field">
            <button type="submit" class="btn-submit">정보 수정</button>
        </div>
    </form>
    </main>

</section>
<!-- 컨텐츠 끝 -->

<%@ include file="/WEB-INF/views/common/footer.jsp" %>