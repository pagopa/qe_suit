package it.pagopa.kernel.security;

public record AccessToken(String jwt, TokenType tokenType, Integer expiresIn) {
}
