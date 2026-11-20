package com.mycompany.gamehunt.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Enkripsi sandi dengan SHA-256. */
public class Password {
    public static String hash(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
