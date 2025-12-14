package com.yurt.security;


public class PasswordHasher {

    public static String hashPassword(String plainPassword) {

        return "BCRYPT_HASH_" + plainPassword.hashCode();
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {

        return hashedPassword.equals("BCRYPT_HASH_" + plainPassword.hashCode());
    }
}