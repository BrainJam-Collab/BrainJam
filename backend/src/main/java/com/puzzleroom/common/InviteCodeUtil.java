package com.puzzleroom.common;

import java.security.SecureRandom;

public final class InviteCodeUtil {
    private static final SecureRandom RNG = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private InviteCodeUtil() {}

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RNG.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(idx));
        }
        return sb.toString();
    }
}
