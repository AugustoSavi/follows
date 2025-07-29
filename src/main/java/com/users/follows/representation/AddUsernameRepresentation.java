package com.users.follows.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddUsernameRepresentation(String username,
                                        @Schema(hidden = true)
                                        String key_imagem_publicacao) {

    public AddUsernameRepresentation {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }
}
