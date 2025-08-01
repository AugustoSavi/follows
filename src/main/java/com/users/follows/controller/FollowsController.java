package com.users.follows.controller;

import com.users.follows.representation.AddUsernameRepresentation;
import com.users.follows.service.FollowsService;
import com.users.follows.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api")
public class FollowsController {

    private final FollowsService followsService;
    private final S3Service s3Service;

    public FollowsController(FollowsService followsService, S3Service s3Service) {
        this.followsService = followsService;
        this.s3Service = s3Service;
    }

    @PostMapping(value = "/add/{username}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> add(
            @PathVariable String username,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {

            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body("Username cannot be null or empty");
            }
            String uploaded = null;
            if (file != null && !file.isEmpty()) {
                log.info("Arquivo recebido: {}", file.getOriginalFilename());
                uploaded = s3Service.upload(file);
                log.info("Arquivo enviado para o S3: {}", uploaded);
                if (uploaded == null || uploaded.isEmpty()) {
                    return ResponseEntity.status(500).body("Failed to upload file to S3");
                }
            }

            AddUsernameRepresentation addUsernameRepresentation = new AddUsernameRepresentation(username, uploaded);

            followsService.addNewUser(addUsernameRepresentation);

            return ResponseEntity.ok("Following search initiated for user: " + addUsernameRepresentation.username());
        } catch (Exception e) {
            log.error("Error processing request: {}", e.getMessage());
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }

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
