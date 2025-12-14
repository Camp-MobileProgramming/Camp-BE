package com.Camp.service;

import com.Camp.domain.profile.ProfileMemo;
import com.Camp.domain.profile.ProfileMemoRepository;
import com.Camp.dto.profile.ProfileMemoResponse;
import com.Camp.dto.profile.ProfileMemoUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileMemoService {

    private final ProfileMemoRepository memoRepository;

    @Transactional(readOnly = true)
    public ProfileMemoResponse getMemo(String ownerNickname, String targetNickname) {
        return memoRepository.findByOwnerNicknameAndTargetNickname(ownerNickname, targetNickname)
                .map(memo -> ProfileMemoResponse.builder()
                        .targetNickname(targetNickname)
                        .content(memo.getContent())
                        .build())
                .orElse(null); // 없으면 null 리턴 → 컨트롤러에서 404 or 빈 문자열 처리
    }

    @Transactional
    public ProfileMemoResponse upsertMemo(String ownerNickname, String targetNickname, ProfileMemoUpdateRequest req) {
        ProfileMemo memo = memoRepository
                .findByOwnerNicknameAndTargetNickname(ownerNickname, targetNickname)
                .orElseGet(() -> new ProfileMemo(ownerNickname, targetNickname, ""));

        memo.updateContent(req.getContent() == null ? "" : req.getContent());
        ProfileMemo saved = memoRepository.save(memo);

        return ProfileMemoResponse.builder()
                .targetNickname(saved.getTargetNickname())
                .content(saved.getContent())
                .build();
    }
}
