package com.gym.moduleapi.api.post.controller;

import com.gym.moduleapi.api.post.request.PostCommentRequest;
import com.gym.moduleapi.api.post.request.PostCreateRequest;
import com.gym.moduleapi.api.post.request.PostModifyRequest;
import com.gym.moduleapi.api.post.response.CommentResponse;
import com.gym.moduleapi.api.post.response.PostResponse;
import com.gym.modulecore.core.post.model.dto.Post;
import com.gym.modulecore.core.post.service.PostService;
import com.gym.modulecore.core.user.model.dto.User;
import com.gym.modulecore.resolver.annotation.UserInfo;
import com.gym.modulecore.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public CommonResponse<Void> create(@RequestBody PostCreateRequest request, @UserInfo User user) {
        postService.create(request.getTitle(), request.getBody(), user.getId());
        return CommonResponse.success();
    }

    @PutMapping("/{postId}")
    public CommonResponse<PostResponse> modify(@PathVariable Long postId, @RequestBody PostModifyRequest request, @UserInfo User user) {
        Post post = postService.modify(request.getTitle(), request.getBody(), user.getId(), postId);
        return CommonResponse.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public CommonResponse<Void> delete(@PathVariable Long postId, @UserInfo User user) {
        postService.delete(user.getId(), postId);
        return CommonResponse.success();
    }

    @GetMapping
    public CommonResponse<Page<PostResponse>> list(Pageable pageable) {
        return CommonResponse.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public CommonResponse<Page<PostResponse>> my(Pageable pageable, @UserInfo User user) {
        return CommonResponse.success(postService.my(user.getId(), pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public CommonResponse<Void> like(@PathVariable Long postId, @UserInfo User user) {
        postService.like(postId, user.getId());
        return CommonResponse.success();
    }

    @GetMapping("/{postId}/likes")
    public CommonResponse<Long> likeCount(@PathVariable Long postId) {
        return CommonResponse.success(postService.likeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public CommonResponse<Void> comment(@PathVariable Long postId, @RequestBody PostCommentRequest request, @UserInfo User user) {
        postService.comment(postId, user.getId(), request.getComment());
        return CommonResponse.success();
    }

    @GetMapping("/{postId}/comments")
    public CommonResponse<Page<CommentResponse>> comment(@PathVariable Long postId, Pageable pageable) {
        return CommonResponse.success(postService.getComments(postId, pageable).map(CommentResponse::fromComment));
    }
}
