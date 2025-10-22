$(document).ajaxSend(function (e, xhr, options) {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    if (token && header) xhr.setRequestHeader(header, token);
});
$(document).ready(function() {
    //무료 강의 처리
    const $price = $('#price');
    const $isFree = $('#isFree');

    function applyPriceState() {
        if ($isFree.is(':checked')) {
            $price.val(0).prop('disabled', true);
        } else {
            $price.prop('disabled', false);
        }
    }

    $isFree.on('change', applyPriceState);
    applyPriceState();


    //카테고리 연동 처리
    const selectedParentId = $('#selectedParentId').val();
    const selectedChildId = $('#selectedChildId').val();

    const $parent = $("#parentCategory");
    const $child = $("#childCategory");

    $parent.val(selectedParentId);

    function loadChildCategories(parentId, selectedChildId) {
        $child.empty().append('<option value="">하위 카테고리 선택</option>');
        if (!parentId) return;

        $.ajax({
            url: "/category/children",
            data: { parentId },
            success: function(children) {
                if (!children || children.length === 0) {
                    $child.append('<option value="">하위 카테고리 없음</option>');
                    return;
                }
                children.forEach(c => {
                    const selected = c.categoryId == selectedChildId ? "selected" : "";
                    $child.append(`<option value="${c.categoryId}" ${selected}>${c.categoryName}</option>`);
                });
            },
            error: function(xhr) {
                console.error("하위 카테고리 불러오기 실패:", xhr);
            }
        });
    }

    if (selectedParentId) {
        loadChildCategories(selectedParentId, selectedChildId);
    }

    $parent.on("change", function() {
        loadChildCategories($(this).val());
    });

    $("#lectureEditForm").on("submit", function(e) {
        e.preventDefault();

        const formData = new FormData(this);

        $.ajax({
            url: "/admin/lecture/edit",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            dateType: "json",
            success: function(res) {
                console.log("응답 도착:", res);
                console.log("success 값:", res.success);
                if (res.success) {
                    alert("강의 정보가 수정되었습니다. 챕터 수정 페이지로 이동합니다.");
                    console.log("이동 시도:", `/admin/chapter/edit?lectureId=${res.lectureId}`);
                    setTimeout(() => {
                        window.location.href = `/admin/chapter/edit?lectureId=${res.lectureId}`;
                    }, 200);
                } else {
                    alert("수정 실패: " + res.message);
                }
            },
            error: function(xhr) {
                console.error("수정 중 오류:", xhr);
                alert("서버 오류가 발생했습니다.");
            }
        });
    });
    $("#uploadThumbnailBtn").on("click", function () {
        const file = $("#thumbnailFile")[0].files[0];
        if (!file) {
            alert("파일을 선택해주세요!");
            return;
        }

        const formData = new FormData();
        formData.append("lectureId", $("input[name='lectureId']").val());
        formData.append("file", file);

        $.ajax({
            url: "/admin/lecture/thumbnail/upload",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                if (res.success) {
                    $("#lectureThumbnailPreview").attr("src", res.thumbnailUrl);
                    alert("썸네일이 업로드되었습니다!");
                } else {
                    alert("업로드 실패: " + res.message);
                }
            },
            error: function (err) {
                alert("서버 오류가 발생했습니다.");
                console.error(err);
            }
        });
    });

    $("#resetThumbnailBtn").on("click", function () {
        const lectureId = $("input[name='lectureId']").val();
        $.ajax({
            url: "/admin/lecture/thumbnail/reset",
            type: "POST",
            data: { lectureId: lectureId },
            success: function (res) {
                if (res.success) {
                    $("#lectureThumbnailPreview").attr("src", res.thumbnailUrl);
                    alert("썸네일이 기본 이미지로 복원되었습니다!");
                } else {
                    alert("복원 실패: " + res.message);
                }
            },
            error: function (err) {
                alert("서버 오류가 발생했습니다.");
                console.error(err);
            }
        });
    });


});
