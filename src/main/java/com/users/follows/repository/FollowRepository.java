package com.users.follows.repository;


import com.users.follows.model.Follow;
import com.users.follows.model.FollowsId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowsId> {

    boolean existsById(@NotNull FollowsId id);

    void deleteById(@NotNull FollowsId id);
}
