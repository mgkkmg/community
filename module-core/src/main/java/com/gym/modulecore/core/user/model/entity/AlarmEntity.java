package com.gym.modulecore.core.user.model.entity;

import com.gym.modulecore.core.user.model.AlarmArgs;
import com.gym.modulecore.core.user.model.enums.AlarmType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@SQLDelete(sql = "UPDATE alarm SET deleted_at = NOW() where id = ?")
@Where(clause = "deleted_at is NULL")
@Setter
@Getter
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "alarm", indexes = {
        @Index(name = "user_id_idx", columnList = "user_id")
})
@Entity
public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FetchType이 EAGER 인 경우 UserEntity가 조인으로 선언(private UserEntity user;)이 되어 있으면 UserEntity가 사용되지 않더라도 무조건 해당 엔티티 쿼리를 호출하고
    // LAZY 인 경우 선언과 무관하게 실제 UserEntity를 사용할때 쿼리를 호출한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user; // 알람을 받은 사람

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private AlarmArgs args;

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

    public static AlarmEntity of(UserEntity userEntity, AlarmType alarmType, AlarmArgs args) {
        AlarmEntity entity = new AlarmEntity();
        entity.setUser(userEntity);
        entity.setAlarmType(alarmType);
        entity.setArgs(args);
        return entity;
    }
}
