package com.gym.moduleapi.api.post.response;

import com.gym.moduleapi.api.user.response.UserResponse;
import com.gym.modulecore.core.post.model.dto.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String body;
    private UserResponse user;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static PostResponse fromPost(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                UserResponse.fromUser(post.getUser()),
                post.getRegisteredAt(),
                post.getUpdatedAt(),
                post.getDeletedAt()
        );
    }
}
