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
});
