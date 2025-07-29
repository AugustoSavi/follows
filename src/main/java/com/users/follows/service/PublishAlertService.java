package com.users.follows.service;

import com.errorxcode.jxinsta.JxInsta;
import com.errorxcode.jxinsta.endpoints.profile.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PublishAlertService {

    private final JxInsta instagram;

    public PublishAlertService(JxInsta instagram) {
        this.instagram = instagram;
    }

    public void publishAlert(String username, String followingUsername) {
        try {
            Profile profileUsername = instagram.getProfile(username);
            String profilePicUrl = profileUsername.profile_pic_url;

            Profile profileFollowing = instagram.getProfile(followingUsername);
            String followingProfilePicUrl = profileFollowing.profile_pic_url;

            String alertMessage = String.format("New follow alert: %s is now following %s. Profile picture: %s, Following profile picture: %s",
                    profileUsername.username, profileFollowing.username, profilePicUrl, followingProfilePicUrl);

            log.info("New follow alert: {}", alertMessage);

        } catch (Exception e) {
            log.error("error buscando profiles: {}", e.getMessage());
        }
    }
}
