<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ include file="/WEB-INF/views/common/header.jsp" %>

<link rel="stylesheet" href="/css/mypage.css">
<link rel="stylesheet" href="/css/form.css">

<!-- CSRF -->
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<style>
    [data-msg] {
        display: block;
        margin-top: 4px;
        font-size: 0.85rem;
    }

    [data-msg].error {
        color: #dc3545;
    }

    [data-msg].valid {
        color: #198754;
    }

    .btn-submit:disabled {
        background: #ccc !important;
        border-color: #bbb;
        cursor: not-allowed;
        opacity: 0.7;
    }

</style>

<!-- 컨텐츠 -->
<section id="mypage" class="container">

    <%@ include file="/WEB-INF/views/common/sideMenu.jsp" %>

    <!-- 컨텐츠 -->
    <main class="main-content">
        <h3>내 정보</h3>

        <form id="signupForm" method="post" action="update" novalidate>

            <!-- CSRF -->
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

            <!-- 이메일 -->
            <div class="field">
                <label for="email" class="form-label"><span class="required-mark">*</span> 이메일</label>
                <input
                    type="email"
                    class="form-control"
                    id="email"
                    name="email"
                    placeholder="name@example.com"
                    readonly
                    required
                    disabled
                    value="${mypage.email}"
                />
            </div>

            <!-- 비밀번호 -->
            <div class="field">
                <label for="password" class="form-label"><span class="required-mark">*</span> 비밀번호</label>
                    <a href="/mypage/password" class="btn-outline-main">비밀번호 바꾸기</a>
            </div>

            <!-- 닉네임 -->
            <div class="field">
                <label for="nickname" class="form-label"><span class="required-mark">*</span> 닉네임</label>
                <input type="text" class="form-control" id="nickname" name="nickname" required
                       value="${mypage.nickname}"/>
                <span data-msg="nickname"></span>
            </div>


            <!-- 이름 -->
            <div class="field">
                <label for="name" class="form-label"><span class="required-mark">*</span> 이름</label>
                <input
                        type="text"
                        class="form-control"
                        id="username"
                        name="username"
                        placeholder="홍길동"
                        required
                        value="${mypage.username}"
                />
                <span data-msg="username"></span>
            </div>

            <!-- 전화번호 -->
            <div class="field">
                <label for="phone" class="form-label">전화번호</label>
                <input
                    type="tel"
                    class="form-control"
                    id="phone"
                    name="phone"
                    required
                    value="${mypage.phone}"
                />
                <span data-msg="phone"></span>
            </div>

            <!-- 생년월일 -->
            <div class="field">
                <label for="birth" class="form-label">생년월일</label>
                <input type="date" class="form-control" id="birth" name="birth" required value="${mypage.birth}"/>
                <span data-msg="birth"></span>
            </div>

            <!-- 성별 -->
            <div class="field">
                <label class="form-label d-block">성별</label>
                <div class="gender-group">
                    <label class="gender-radio">
                        <input type="radio" name="gender" value="M" ${mypage.gender == 'M' ? 'checked' : ''}>
                        <span>남성</span>
                    </label>
                    <label class="gender-radio">
                        <input type="radio" name="gender" value="F" ${mypage.gender == 'F' ? 'checked' : ''}>
                        <span>여성</span>
                    </label>
                </div>
            </div>

            <!-- 버튼 -->
            <div class="field">
                <button type="submit" class="btn-main">정보 수정</button>
            </div>
        </form>
    </main>

</section>
<!-- 컨텐츠 끝 -->

<script src="/js/mypage-info.js"></script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>

