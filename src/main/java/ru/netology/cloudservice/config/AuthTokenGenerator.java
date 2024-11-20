package ru.netology.cloudservice.config;

import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Base64;

@NoArgsConstructor
public class AuthTokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom(); // можно также использовать ThreadLocalRandom
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public  String generateToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
