package com.kobeay.couple_schedule.server.user.util;

import java.security.SecureRandom;

public class InviteCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int CODE_LENGTH = 8;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate() {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

}
