package com.users.follows.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "follows_alerts")
public class FollowAlert {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String follows;
    private String alertMessage;
    private LocalDateTime createdAt;

    public FollowAlert(String username, String follows, String alertMessage) {
        this.username = username;
        this.follows = follows;
        this.alertMessage = alertMessage;
        this.createdAt = LocalDateTime.now();
    }
}
