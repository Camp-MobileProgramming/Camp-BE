package com.Camp.domain.profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "profile_memos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_memo_owner_target",
                        columnNames = {"owner_nickname", "target_nickname"}
                )
        }
)
@Getter
@NoArgsConstructor
public class ProfileMemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메모 작성자 (나)
    @Column(name = "owner_nickname", nullable = false, length = 50)
    private String ownerNickname;

    // 메모 대상 (상대 프로필 주인)
    @Column(name = "target_nickname", nullable = false, length = 50)
    private String targetNickname;

    @Lob
    @Column(nullable = false)
    private String content;

    private LocalDateTime updatedAt;

    public ProfileMemo(String ownerNickname, String targetNickname, String content) {
        this.ownerNickname = ownerNickname;
        this.targetNickname = targetNickname;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
