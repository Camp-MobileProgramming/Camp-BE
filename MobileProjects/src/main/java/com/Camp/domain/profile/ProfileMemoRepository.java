package com.Camp.domain.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileMemoRepository extends JpaRepository<ProfileMemo, Long> {

    Optional<ProfileMemo> findByOwnerNicknameAndTargetNickname(String ownerNickname, String targetNickname);
}
