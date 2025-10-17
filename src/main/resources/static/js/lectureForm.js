document.addEventListener("DOMContentLoaded", () => {
    // 무료강의 체크박스
    const priceInput = document.getElementById("price");
    const freeCheck = document.getElementById("isFree");

    if (freeCheck && priceInput) {
        if (freeCheck.checked) {
            priceInput.value = 0;
            priceInput.disabled = true;
        }

        freeCheck.addEventListener("change", () => {
            if (freeCheck.checked) {
                priceInput.value = 0;
                priceInput.disabled = true;
            } else {
                priceInput.disabled = false;
            }
        });
    }

    // 상위 카테고리 → 하위 카테고리 AJAX 로드
    const parentSelect = $('#parentCategory');
    const childSelect = $('#childCategory');

    parentSelect.change(function() {
        const parentId = $(this).val();

        if (!parentId) {
            childSelect.empty().append('<option value="">하위 카테고리 선택</option>');
            return;
        }

        $.ajax({
            url: `/category/children?parentId=${parentId}`,
            type: 'GET',
            success: function(list) {
                childSelect.empty();

                if (!list || list.length === 0) {
                    childSelect.append('<option value="">하위 카테고리 없음</option>');
                } else {
                    childSelect.append('<option value="">하위 카테고리 선택</option>');
                    list.forEach(c => {
                        childSelect.append(`<option value="${c.categoryId}">${c.categoryName}</option>`);
                    });
                }
            },
            error: function(xhr) {
                console.error("하위 카테고리 로드 실패:", xhr);
                alert('하위 카테고리를 불러오지 못했습니다.');
            }
        });
    });
});