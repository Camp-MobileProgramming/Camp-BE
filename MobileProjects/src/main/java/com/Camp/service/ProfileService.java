package com.Camp.service;

import com.Camp.domain.profile.Profile;
import com.Camp.domain.profile.ProfileRepository;
import com.Camp.dto.profile.ProfileResponse;
import com.Camp.dto.profile.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String nickname) {
        Profile profile = profileRepository.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 닉네임의 프로필이 없습니다: " + nickname));

        return ProfileResponse.from(profile);
    }

    @Transactional
    public ProfileResponse upsertProfile(String nickname, ProfileUpdateRequest req) {
        Profile profile = profileRepository.findByNickname(nickname)
                .orElseGet(() -> new Profile(nickname));

        String interests = listToString(req.getInterests());

        profile.update(
                req.getStatusMessage(),
                req.getIntro(),
                interests,
                req.getFriends() // friends만 업데이트됨
        );

        return ProfileResponse.from(profileRepository.save(profile));
    }

    private String listToString(java.util.List<String> list) {
        if (list == null || list.isEmpty()) return null;
        StringJoiner joiner = new StringJoiner(",");
        list.stream().filter(s -> s != null && !s.isBlank()).map(String::trim).forEach(joiner::add);
        return joiner.toString();
    }
}
