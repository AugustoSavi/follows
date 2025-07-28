package com.users.follows.controller;

import com.users.follows.service.FollowsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FollowsController {

    private final FollowsService followsService;

    public FollowsController(FollowsService followsService) {
        this.followsService = followsService;
    }

    @PostMapping("/add/{username}")
    public ResponseEntity<String> add(@PathVariable String username) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be null or empty");
        }
        followsService.addNewUser(username);
        return ResponseEntity.ok("Following search initiated for user: " + username);
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
