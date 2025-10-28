// src/main/java/com/livo/project/notice/repository/NoticeRepository.java
package com.livo.project.notice.repository;

import com.livo.project.admin.domain.dto.NoticeListDto;
import com.livo.project.notice.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    List<Notice> findAllByOrderByCreatedAtDesc();
    List<Notice> findAllByOrderByIsPinnedDescCreatedAtDesc();
    Page<Notice> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);
    List<Notice> findTop5ByIsVisibleTrueOrderByIsPinnedDescCreatedAtDesc();
    List<Notice> findTop5ByOrderByCreatedAtDesc();

    //  일반 사용자용: 공개글만, "오래된 순(asc)"
    @Query("""
    select new com.livo.project.admin.domain.dto.NoticeListDto(
        n.id,
        n.title,
        coalesce(u.nickname, n.writer),
        n.createdAt,
        n.isPinned,
        n.isVisible,
        n.viewCount,
        n.content
    )
    from Notice n
    left join com.livo.project.auth.domain.entity.User u
           on u.email = n.writer
    where n.isVisible = true
    order by n.createdAt desc
""")
    Page<NoticeListDto> findNoticeListWithNickname(Pageable pageable);


    // 관리자용: 검색 + 페이징(정렬은 기존처럼 최신순 유지)
    @Query("""
        select new com.livo.project.admin.domain.dto.NoticeListDto(
            n.id,
            n.title,
            coalesce(u.nickname, n.writer),
            n.createdAt,
            n.isPinned,
            n.isVisible,
            n.viewCount,
            n.content
        )
        from Notice n
        left join com.livo.project.auth.domain.entity.User u
               on u.email = n.writer
        where (:q is null or :q = ''
            or lower(n.title)   like lower(concat('%', :q, '%'))
            or lower(n.content) like lower(concat('%', :q, '%')))
        order by n.isPinned desc, n.createdAt desc
    """)
    Page<NoticeListDto> findAdminList(@Param("q") String q, Pageable pageable);

    //  상세 페이지용: id로 작성자 닉네임만 얻기
    @Query("""
        select coalesce(u.nickname, n.writer)
        from Notice n
        left join com.livo.project.auth.domain.entity.User u
               on u.email = n.writer
        where n.id = :id
    """)
    String findAuthorNameById(@Param("id") int id);
}
