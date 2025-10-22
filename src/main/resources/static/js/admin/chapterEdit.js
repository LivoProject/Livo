$(document).ajaxSend(function (e, xhr, options) {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    if (token && header) xhr.setRequestHeader(header, token);
});

$(document).ready(function () {
    const lectureId = $("#lectureId").val();
    let chapterIndex = $(".chapter").length;

    // 페이지 로드시 기존 챕터 목록 불러오기
    if (lectureId) {
        $.ajax({
            url: `/admin/chapter/list/${lectureId}`,
            type: "GET",
            success: function (chapters) {
                const container = $("#chapterContainer");
                container.empty();

                if (!chapters || chapters.length === 0) {
                    container.append(`
                        <p class="text-muted text-center">등록된 챕터가 없습니다.</p>
                    `);
                    return;
                }

                // 기존 챕터 데이터 렌더링
                chapters.forEach((c, i) => {
                    const html = `
                        <div class="chapter border rounded p-3 mb-3">
                            <input type="hidden" id="chapterId" value="${c.chapterId}" />
                            <input type="hidden" class="chapterOrder" value="${c.chapterOrder}" />

                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <strong>Chapter <span class="chapter-index">${c.chapterOrder}</span></strong>
                                <span class="text-muted small">(드래그하여 순서 변경 가능)</span>
                            </div>

                            <label>챕터명</label>
                            <input type="text" class="form-control mt-2 mb-2 chapterName" value="${c.chapterName}" />

                            <label>유튜브 URL</label>
                            <input type="text" class="form-control mt-2 mb-2 youtubeUrl" value="${c.youtubeUrl}" />

                            <label>내용</label>
                            <textarea class="form-control content mt-2" rows="3">${c.content || ""}</textarea>
                        </div>`;
                    container.append(html);
                });
                // 불러온 데이터 기준으로 인덱스 갱신
                chapterIndex = chapters.length;
            },
            error: function (xhr) {
                console.error("챕터 불러오기 실패:", xhr);
                alert("기존 챕터를 불러오는 중 오류가 발생했습니다.");
            }
        });
    }
    // 챕터 순서 정렬 (드래그)
    $("#chapterContainer").sortable({
        placeholder: "chapter-placeholder",
        update: function () {
            updateChapterOrder();
        }
    });

    // 챕터 추가 버튼
    $("#addChapterBtn").click(function () {
        chapterIndex++;
        const html = `
            <div class="chapter border rounded p-3 mb-3">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <strong>Chapter <span class="chapter-index">${chapterIndex}</span></strong>
                    <span class="text-muted small">(드래그하여 순서 변경 가능)</span>
                </div>

                <label>챕터명</label>
                <input type="text" class="form-control mt-2 mb-2 chapterName" />

                <input type="hidden" class="chapterOrder" value="${chapterIndex}" />

                <label>유튜브 URL</label>
                <input type="text" class="form-control mt-2 mb-2 youtubeUrl" />

                <label>내용</label>
                <textarea class="form-control content mt-2" rows="3"></textarea>
            </div>`;
        $("#chapterContainer").append(html);
        updateChapterOrder();
    });

    // 순서 업데이트 함수
    function updateChapterOrder() {
        $(".chapter").each(function (index) {
            const order = index + 1;
            $(this).find(".chapterOrder").val(order);
            $(this).find(".chapter-index").text(order);
        });
    }

    // 챕터 데이터 수집
    function collectChapterData() {
        const chapters = [];
        $(".chapter").each(function () {
            const chapterId = $(this).find("#chapterId").val() || null;
            chapters.push({
                chapterId: chapterId,
                lectureId: lectureId,
                chapterName: $(this).find(".chapterName").val(),
                chapterOrder: $(this).find(".chapterOrder").val(),
                youtubeUrl: $(this).find(".youtubeUrl").val(),
                content: $(this).find(".content").val()
            });
        });
        return chapters;
    }

    // 수정 완료 버튼
    $("#editChapterBtn").click(function () {
        const chapters = collectChapterData();

        if (!chapters || chapters.length === 0) {
            alert("최소 1개 이상의 챕터를 등록해주세요!");
            return;
        }

        $.ajax({
            url: "/admin/chapter/edit",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(chapters),
            success: function (res) {
                if (res.success) {
                    alert("챕터 수정이 완료되었습니다!");
                    window.location.href = "/admin/lecture";
                } else {
                    alert("수정 실패: " + res.message);
                }
            },
            error: function (xhr) {
                console.error("수정 오류:", xhr);
                alert("수정 중 오류가 발생했습니다.");
            }
        });
    });
});
