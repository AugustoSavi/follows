package com.users.follows.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserRepresentation(Long pk, String username, String full_name) {
}
