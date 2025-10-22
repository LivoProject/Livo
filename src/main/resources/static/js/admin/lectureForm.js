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
            }
        });
    });

    // 강의 폼 데이터 수집
    function getLectureFormData() {
        return {
            title: $('input[name="title"]').val(),
            tutorName: $('input[name="tutorName"]').val(),
            tutorInfo: $('textarea[name="tutorInfo"]').val(),
            totalCount: $('input[name="totalCount"]').val(),
            price: $('input[name="price"]').val(),
            isFree: $('#isFree').is(':checked'),
            reservationStart: $('input[name="reservationStart"]').val(),
            reservationEnd: $('input[name="reservationEnd"]').val(),
            lectureStart: $('input[name="lectureStart"]').val(),
            lectureEnd: $('input[name="lectureEnd"]').val(),
            content: $('#summernote').val(),
            category: {
                categoryId: $('#childCategory').val()
            }
        };
    }
    // 다음 단계(챕터 등록) 이동 버튼
    const nextStepBtn = document.getElementById("nextStepBtn");
    if (nextStepBtn) {
        nextStepBtn.addEventListener("click", (e) => {
            e.preventDefault();

            const lectureData = getLectureFormData();

            if (!lectureData.category.categoryId) {
                alert("카테고리를 선택해주세요.");
                return;
            }

            sessionStorage.setItem('tempLecture', JSON.stringify(lectureData));
            sessionStorage.setItem('categoryId', lectureData.category.categoryId);
            console.log("categoryId:", sessionStorage.getItem("categoryId"));
            alert("챕터 등록 단계로 이동합니다.");
            location.href = '/admin/chapter/form';
        });
    }
});
