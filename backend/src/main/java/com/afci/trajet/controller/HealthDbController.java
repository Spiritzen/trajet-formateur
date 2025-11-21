package com.afci.trajet.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.afci.trajet.repository.UtilisateurRepository;

@RestController
public class HealthDbController {

    private final UtilisateurRepository utilisateurRepository;

    public HealthDbController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/api/health/db")
    public String checkDb() {
        long count = utilisateurRepository.count();
        return "DB OK – nb utilisateurs = " + count;
    }
}