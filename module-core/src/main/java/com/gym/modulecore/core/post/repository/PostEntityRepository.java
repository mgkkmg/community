package com.gym.modulecore.core.post.repository;

import com.gym.modulecore.core.post.model.entity.PostEntity;
import com.gym.modulecore.core.user.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findAllByUser(UserEntity userEntity, Pageable pageable);
}
