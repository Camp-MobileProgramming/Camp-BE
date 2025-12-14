package com.Camp.domain.settings;

import com.Camp.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // 위치 공유 여부
    private boolean locationShare;

    // all, friends, none
    @Enumerated(EnumType.STRING)
    private LocationVisibility locationVisibility;

    // 채팅 알림
    private boolean chatAlarm;

    // 캠프(친구) 요청 알림
    private boolean campRequestAlarm;

    public static UserSettings createDefault(User user) {
        return UserSettings.builder()
                .user(user)
                .locationShare(true)
                .locationVisibility(LocationVisibility.ALL)
                .chatAlarm(true)
                .campRequestAlarm(true)
                .build();
    }

    public void update(
            Boolean locationShare,
            LocationVisibility visibility,
            Boolean chatAlarm,
            Boolean campRequestAlarm
    ) {
        if (locationShare != null) this.locationShare = locationShare;
        if (visibility != null) this.locationVisibility = visibility;
        if (chatAlarm != null) this.chatAlarm = chatAlarm;
        if (campRequestAlarm != null) this.campRequestAlarm = campRequestAlarm;
    }
}
