package com.gym.modulecore.core.post.repository;

import com.gym.modulecore.core.post.model.entity.CommentEntity;
import com.gym.modulecore.core.post.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, Integer> {

    // Page 인터페이스는 페이징에 대한 응답값을 담고, Pageable 인터페이스는 페이징에 대한 요청값을 담는다.
    Page<CommentEntity> findAllByPost(PostEntity postEntity, Pageable pageable);

    // JPA에 delete 같은 경우 delete 전 해당 데이터의 존재 여부를 select 한 후 삭제하기 때문에 불필요한 DB I/O 가 발생하여,
    // @Query를 사용하여 바로 delete 처리하는 것이 효율적이다.
    @Transactional
    @Modifying // INSERT, DELETE, UPDATE를 네이티브 쿼리로 작성하려면 해당 어노테이션 필요
    @Query("UPDATE CommentEntity entity SET deleted_at = NOW() where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);
}
