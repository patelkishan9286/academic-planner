package com.group13.academicplannerbackend.util;

public class Constants {
    private static final int VERIFICATION_CODE_EXPIRY = 30;
    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    public static int getVerificationCodeExpiryMinutes() {
        return VERIFICATION_CODE_EXPIRY;
    }
    public static String getJwtTokenPrefix() {
        return JWT_TOKEN_PREFIX;
    }

    
}
