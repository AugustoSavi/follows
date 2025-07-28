package com.users.follows.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "follows")
public class Follow {

    @EmbeddedId
    private FollowsId id;
    private String username;
    private String follows;
    private LocalDateTime criadoEm;
    @Version
    private long version = 0L;

    public Follow(Long followerId, String username, Long followedId, String follows) {
        this.id = new FollowsId(followerId, followedId);
        this.username = username;
        this.follows = follows;
        this.criadoEm = LocalDateTime.now();
    }
}
