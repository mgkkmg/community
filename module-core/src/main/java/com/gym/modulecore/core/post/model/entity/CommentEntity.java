package com.gym.modulecore.core.post.model.entity;

import com.gym.modulecore.core.user.model.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@SQLDelete(sql = "UPDATE comment SET deleted_at = NOW() where id = ?")
@Where(clause = "deleted_at is NULL")
@Setter
@Getter
@Table(name = "comment", indexes = {
        @Index(name = "post_id_idx", columnList = "post_id")
})
@Entity
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 하나의 User에서 여러개의 like
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne // 하나의 Post에서 여러개의 like
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @Column(name = "comment")
    private String comment;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static CommentEntity of(UserEntity userEntity, PostEntity postEntity, String comment) {
        CommentEntity entity = new CommentEntity();
        entity.setUser(userEntity);
        entity.setPost(postEntity);
        entity.setComment(comment);
        return entity;
    }
}
