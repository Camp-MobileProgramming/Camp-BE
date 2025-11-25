package com.Camp.domain.friendship;

import com.Camp.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "friendship",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"owner_id", "friend_id"})
        }
)
public class FriendShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 나 (친구 목록의 주인)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // 친구
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    public FriendShip(User owner, User friend) {
        this.owner = owner;
        this.friend = friend;
    }
}
