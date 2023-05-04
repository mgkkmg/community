package com.gym.modulecore.core.post.service;

import com.gym.modulecore.core.post.model.entity.CommentEntity;
import com.gym.modulecore.core.post.model.entity.LikeEntity;
import com.gym.modulecore.core.post.model.entity.PostEntity;
import com.gym.modulecore.core.post.repository.CommentEntityRepository;
import com.gym.modulecore.core.post.repository.LikeEntityRepository;
import com.gym.modulecore.core.post.repository.PostEntityRepository;
import com.gym.modulecore.core.user.model.entity.AlarmEntity;
import com.gym.modulecore.core.user.model.entity.UserEntity;
import com.gym.modulecore.core.user.repository.AlarmEntityRepository;
import com.gym.modulecore.core.user.repository.UserEntityRepository;
import com.gym.modulecore.exception.CommunityException;
import com.gym.modulecore.exception.ErrorCode;
import fixture.PostEntityFixture;
import fixture.UserEntityFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostEntityRepository postEntityRepository;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private LikeEntityRepository likeEntityRepository;

    @Mock
    private CommentEntityRepository commentEntityRepository;

    @Mock
    private AlarmEntityRepository alarmEntityRepository;

    @Test
    void 포스트작성이_성공한_경우() {
        String title = "title";
        String body = "body";
        Long userId = 1L;

        // mocking
//        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // assertDoesNotThrow: 어떤 종류의 예외도 발생하지 않을 경우를 검증하기 위하여 사용
        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userId));
    }

//    @Test
//    void 포스트작성시_요청한유저가_존재하지_않는_경우() {
//        String title = "title";
//        String body = "body";
//        Long userId = 1L;
//
//        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
//
//        CommunityException e = Assertions.assertThrows(CommunityException.class, () -> postService.create(title, body, userId));
//        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
//    }

    @Test
    void 포스트수정이_성공한_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        // assertDoesNotThrow: 어떤 종류의 예외도 발생하지 않을 경우를 검증하기 위하여 사용
        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userId, postId));
    }

    @Test
    void 포스트수정시_포스트가_존재하지않는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        CommunityException e = Assertions.assertThrows(CommunityException.class, () -> postService.modify(title, body, userId, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트수정시_권한이_없는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity writer = UserEntityFixture.get("userName1", "password", 2L);

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        CommunityException e = Assertions.assertThrows(CommunityException.class, () -> postService.modify(title, body, userId + 1, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 포스트삭제가_성공한_경우() {
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // assertDoesNotThrow: 어떤 종류의 예외도 발생하지 않을 경우를 검증하기 위하여 사용
        Assertions.assertDoesNotThrow(() -> postService.delete(userId, postId));
    }

    @Test
    void 포스트삭제시_포스트가_존재하지않는_경우() {
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        CommunityException e = Assertions.assertThrows(CommunityException.class, () -> postService.delete(userId, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트삭제시_권한이_없는_경우() {
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity writer = UserEntityFixture.get("userName1", "password", 2L);

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        CommunityException e = Assertions.assertThrows(CommunityException.class, () -> postService.delete(userId + 1, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 피드목록요청이_성공한_경우() {
        Pageable pageable = mock(Pageable.class);

        // mocking
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

        // assertDoesNotThrow: 어떤 종류의 예외도 발생하지 않을 경우를 검증하기 위하여 사용
        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 내_피드목록요청이_성공한_경우() {
        Long userId = 1L;
        Pageable pageable = mock(Pageable.class);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(any());

        // mocking
//        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findAllByUser(userEntity, eq(pageable))).thenReturn(Page.empty());

        // assertDoesNotThrow: 어떤 종류의 예외도 발생하지 않을 경우를 검증하기 위하여 사용
        Assertions.assertDoesNotThrow(() -> postService.my(userId, pageable));
    }

    @Test
    void 좋아요기능이_성공한_경우() {
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(likeEntityRepository.findByUserAndPost(any(), eq(postEntity))).thenReturn(Optional.empty());
        when(likeEntityRepository.save(any())).thenReturn(mock(LikeEntity.class));
        when(alarmEntityRepository.save(any())).thenReturn(mock(AlarmEntity.class));

        // assertDoesNotThrow: 어떤 종류의 예외도 발생하지 않을 경우를 검증하기 위하여 사용
        Assertions.assertDoesNotThrow(() -> postService.like(postId, userId));
    }

    @Test
    void 좋아요시_포스트가_존재하지않는_경우() {
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        CommunityException e = Assertions.assertThrows(CommunityException.class, () -> postService.like(postId, userId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 이미_좋아요를_클릭했을_경우() {
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);
        UserEntity userEntity = postEntity.getUser();

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(likeEntityRepository.findByUserAndPost(any(), eq(postEntity))).thenReturn(Optional.of(mock(LikeEntity.class)));

        CommunityException e = Assertions.assertThrows(CommunityException.class, () -> postService.like(postId, userId));
        Assertions.assertEquals(ErrorCode.ALREADY_LIKED, e.getErrorCode());
    }

    @Test
    void 포스트의_좋아요_개수_가져오기() {
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);

        // mocking
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(likeEntityRepository.countByPost(postEntity)).thenReturn(1L);

        Assertions.assertEquals(1, postService.likeCount(postId));
    }

    @Test
    void 댓글등록이_성공한_경우() {
        String userName = "userName";
        Long userId = 1L;
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, userId);

        // mocking
//        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(commentEntityRepository.save(any())).thenReturn(mock(CommentEntity.class));
        when(alarmEntityRepository.save(any())).thenReturn(mock(AlarmEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.comment(postId, userId, "comment"));
    }

    @Test
    void 댓글목록_조회요청이_성공한_경우() {
        String userName = "userName";
        Long postId = 1L;
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1L);
        Pageable pageable = mock(Pageable.class);

        // mocking
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(commentEntityRepository.findAllByPost(postEntity, pageable)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> postService.getComments(postId, pageable));
    }
}
