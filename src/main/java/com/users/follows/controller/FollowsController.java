package com.users.follows.controller;

import com.users.follows.service.FollowsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/follows")
public class FollowsController {

    private final FollowsService followsService;

    public FollowsController(FollowsService followsService) {
        this.followsService = followsService;
    }

    @PostMapping("/update/{username}")
    public ResponseEntity<String> update(@PathVariable String username) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be null or empty");
        }
        followsService.updateNewFollowings(username);
        return ResponseEntity.ok("Following update initiated for user: " + username);
    }
}
