<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/admin/sidebar.jsp" %>

<main class="main-content position-relative max-height-vh-100 h-100 mt-1 border-radius-lg ps ps--active-y">
    <%@ include file="/WEB-INF/views/admin/navbar.jsp" %>
    <!-- 메인 콘텐츠 -->
    <div class="container-fluid py-4 px-5">
        <div class="form-section">
            <h5 class="mb-4 fw-bold">강의 등록</h5>

            <form action="/lecture/save" method="post" enctype="multipart/form-data">

                <!-- 기본 정보 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">강의 제목</label>
                        <input type="text" name="title" class="form-control" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강사 이름</label>
                        <input type="text" name=tutorName class="form-control" required>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label">강사 소개</label>
                    <textarea name="tutorInfo" class="form-control" rows="2"></textarea>
                </div>

                <!-- 카테고리 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">상위 카테고리</label>
                        <!--카테고리 레벨이1일때-->
                        <select id="parentCategory" class="form-select">
                            <option>IT</option>
                            <option>자기계발</option>
                            <option>문화여가</option>
                            <option>건강</option>
                            <option>언어</option>
                            <option>인문사회</option>
                            <option>자격증</option>
                            <option>경제</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">하위 카테고리</label>
                        <!--카테고리 레벨이2일때-->
                        <select id="childCategory" name="categoryId" class="form-select">

                        </select>
                    </div>
                </div>

                <!-- 강의 인원, 비용 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">모집 인원</label>
                        <input type="number" name="totalCount" class="form-control" min="1" value="10">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강의비</label>
                        <div class="input-group">
                            <input type="number" name="fee" class="form-control" value="0">
                            <div class="input-group-text">
                                <input type="checkbox" id="freeCheck" name="free">
                                <label for="freeCheck" class="ms-1 mb-0">무료강의</label>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 기간 -->
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">예약 시작일</label>
                        <input type="datetime-local" name="reservationStart" class="form-control">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">예약 종료일</label>
                        <input type="datetime-local" name="reservationEnd" class="form-control">
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-6">
                        <label class="form-label">강의 시작일</label>
                        <input type="date" name="lectureStart" class="form-control">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label">강의 종료일</label>
                        <input type="date" name="lectureEnd" class="form-control">
                    </div>
                </div>

                <!-- 강의 내용 -->
                <div class="mb-4">
                    <label class="form-label">강의 내용</label>
                    <textarea id="summernote" name="content"></textarea>
                </div>

                <button class="btn btn-primary w-sm-100" style="max-width: 200px;">등록하기</button>
            </form>
        </div>
    </div>
</main>
</div>
</div>
<!-- Summernote -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-lite.min.js"></script>
<script>
    $(document).ready(function () {
        $('#summernote').summernote({
            height: 300,
            lang: 'ko-KR',
            placeholder: '강의 내용을 입력하세요...',
            callbacks: {
                onImageUpload: function(files) {
                    uploadImage(files[0]);
                }
            }
        });

        function uploadImage(file) {
            let data = new FormData();
            data.append("file", file);

            $.ajax({
                url: "/lecture/uploadImage",
                type: "POST",
                data: data,
                contentType: false,
                processData: false,
                success: function (url) {
                    $('#summernote').summernote('insertImage', url);
                },
                error: function () {
                    alert("이미지 업로드 실패");
                }
            });
        }
    });
</script>
</body>
</html>