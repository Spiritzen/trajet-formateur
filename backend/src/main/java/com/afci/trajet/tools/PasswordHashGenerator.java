package com.afci.trajet.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder(12);

        String rawPassword = "Admin123!"; // le mdp que TU taperas dans Postman
        String hashed = encoder.encode(rawPassword);

        System.out.println("=======================================");
        System.out.println("Mot de passe en clair : " + rawPassword);
        System.out.println("Hash BCrypt : " + hashed);
        System.out.println("=======================================");
    }
}