package com.Camp.domain.profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(length = 255)
    private String statusMessage;

    @Column(length = 1000)
    private String intro;

    @Column(length = 500)
    private String interests; // "스터디,독서" 형태 문자열

    private int friendsCount; // groups · steps 제거

    public Profile(String nickname) {
        this.nickname = nickname;
    }

    public void update(String statusMessage, String intro, String interests, Integer friendsCount) {
        this.statusMessage = statusMessage;
        this.intro = intro;
        this.interests = interests;
        if (friendsCount != null) this.friendsCount = friendsCount;
    }
}
