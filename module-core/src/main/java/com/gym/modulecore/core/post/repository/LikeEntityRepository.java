package com.gym.modulecore.core.post.repository;

import com.gym.modulecore.core.post.model.entity.LikeEntity;
import com.gym.modulecore.core.post.model.entity.PostEntity;
import com.gym.modulecore.core.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByUserAndPost(UserEntity userEntity, PostEntity postEntity);

//    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post = :post")
//    Integer countByPost(@Param("post") PostEntity post);

    Long countByPost(PostEntity post);

    List<LikeEntity> findAllByPost(PostEntity post);

    // JPA에 delete 같은 경우 delete 전 해당 데이터의 존재 여부를 select 한 후 삭제하기 때문에 불필요한 DB I/O 가 발생하여,
    // @Query를 사용하여 바로 delete 처리하는 것이 효율적이다.
    @Transactional
    @Modifying // INSERT, DELETE, UPDATE를 네이티브 쿼리로 작성하려면 해당 어노테이션 필요
    @Query("UPDATE LikeEntity entity SET deleted_at = NOW() where entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);
}
