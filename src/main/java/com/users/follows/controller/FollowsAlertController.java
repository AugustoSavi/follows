package com.users.follows.controller;

import com.users.follows.model.FollowAlert;
import com.users.follows.service.FollowAlertService;
import com.users.follows.service.PublishAlertService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class FollowsAlertController {

    private final FollowAlertService followAlertService;
    private final PublishAlertService publishAlert;

    public FollowsAlertController(FollowAlertService followAlertService, PublishAlertService publishAlert) {
        this.followAlertService = followAlertService;
        this.publishAlert = publishAlert;
    }

    @GetMapping("/search")
    public ResponseEntity<List<FollowAlert>> addFollows(
            @RequestParam String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(List.of(new FollowAlert("Error", "Invalid username", "Username cannot be null or empty")));
        }
        return ResponseEntity.ok(followAlertService.search(username, date, page, size));
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publishAlert(@RequestParam String username, @RequestParam String followingUsername) {
        if (username == null || username.isEmpty() || followingUsername == null || followingUsername.isEmpty()) {
            return ResponseEntity.badRequest().body("Username and followingUsername cannot be null or empty");
        }
        publishAlert.publishAlert(username, followingUsername);
        return ResponseEntity.ok("Follow alert published for " + username + " following " + followingUsername);
    }
}
