package com.users.follows.repository;


import com.users.follows.model.Username;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsernameRepository extends JpaRepository<Username, Long> {

    boolean existsById(@NotNull Long id);

    boolean existsByUsername(@NotNull String username);

}
