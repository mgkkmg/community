package com.gym.modulecore.core.post.service;

import com.gym.modulecore.core.post.model.Comment;
import com.gym.modulecore.core.post.model.Post;
import com.gym.modulecore.core.post.model.entity.CommentEntity;
import com.gym.modulecore.core.post.model.entity.LikeEntity;
import com.gym.modulecore.core.post.model.entity.PostEntity;
import com.gym.modulecore.core.post.repository.CommentEntityRepository;
import com.gym.modulecore.core.post.repository.LikeEntityRepository;
import com.gym.modulecore.core.post.repository.PostEntityRepository;
import com.gym.modulecore.core.user.model.AlarmArgs;
import com.gym.modulecore.core.user.model.entity.AlarmEntity;
import com.gym.modulecore.core.user.model.entity.UserEntity;
import com.gym.modulecore.core.user.model.enums.AlarmType;
import com.gym.modulecore.core.user.repository.AlarmEntityRepository;
import com.gym.modulecore.exception.CommunityException;
import com.gym.modulecore.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;

    @Transactional
    public void create(String title, String body, Long userId) {
        UserEntity userEntity = getUserEntity(userId);

        // post save
        // save() 메소드는 영속성 컨텍스트에 저장하는 것이고 실제로 DB 에 저장은 추후 flush 또는 commit 메소드가 실행될 때 이루어짐
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, Long userId, Long postId) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if (postEntity.getUser().getId() != userEntity.getId()) {
            throw new CommunityException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userId, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        // 변경된 항목 Update
        // saveAndFlush() 메소드는 즉시 DB 에 데이터를 반영함(하나의 트랜잭션 안에서 커밋된 데이터를 다음 로직에서 사용하고자 할때)
        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if (postEntity.getUser().getId() != userEntity.getId()) {
            throw new CommunityException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userId, postId));
        }
        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(Long userId, Pageable pageable) {
        UserEntity userEntity = getUserEntity(userId);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Long postId, Long userId) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = getPostEntityOrException(postId);

        // check liked
        // ifPresent를 통해 like가 이미 존재하고 있으면 throw 예외처리
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new CommunityException(ErrorCode.ALREADY_LIKED, String.format("UserName %s already like post %d", userId, postId));
        });

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));

        // like 후 post 작성자에게 알람 등록
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
    }

    public Long likeCount(Long postId) {
        PostEntity postEntity = getPostEntityOrException(postId);

        // count like
//        List<LikeEntity> likeEntities = likeEntityRepository.findAllByPost(postEntity);
//        return likeEntities.size();
        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Long postId, Long userId, String comment) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = getPostEntityOrException(postId);

        // comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
        // comment 작성 후 post 작성자에게 알람 등록
        alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
    }

    public Page<Comment> getComments(Long postId, Pageable pageable) {
        PostEntity postEntity = getPostEntityOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

    private UserEntity getUserEntity(Long userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        return userEntity;
    }

    // post exist
    private PostEntity getPostEntityOrException(Long postId) {
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new CommunityException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }
}
