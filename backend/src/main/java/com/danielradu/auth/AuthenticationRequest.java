package com.danielradu.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
