package com.users.follows.repository;


import com.users.follows.model.FollowAlert;
import com.users.follows.model.FollowsId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FollowAlertRepository extends JpaRepository<FollowAlert, Long> {

    boolean existsById(@NotNull FollowsId id);

    void deleteById(@NotNull FollowsId id);

    List<FollowAlert> findByUsernameAndCreatedAtAfter(String username, LocalDate date, Pageable pageable);

    List<FollowAlert> findByUsername(String username, Pageable pageable);
}
