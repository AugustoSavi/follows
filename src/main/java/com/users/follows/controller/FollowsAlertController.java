package com.users.follows.controller;

import com.users.follows.model.FollowAlert;
import com.users.follows.service.FollowAlertService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class FollowsAlertController {

    private final FollowAlertService followAlertService;

    public FollowsAlertController(FollowAlertService followAlertService) {
        this.followAlertService = followAlertService;
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
}
