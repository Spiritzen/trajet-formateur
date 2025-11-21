package com.afci.trajet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.afci.trajet.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByCode(String code);
}
