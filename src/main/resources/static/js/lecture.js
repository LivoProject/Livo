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
        // CSRF 토큰 처리 (스프링 시큐리티 활성화 시)
        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");
        $.ajax({
            url: "/admin/lecture/uploadImage",
            type: "POST",
            data: data,
            contentType: false,
            processData: false,
            beforeSend: function(xhr){
                if(token && header) xhr.setRequestHeader(header,token)
            },
            success: function (url) {
                $('#summernote').summernote('insertImage', url);
            },
            error: function (xhr) {
                console.error(xhr)
                alert("이미지 업로드 실패(" + xhr.status + ")");
            }
        });
    }
});
