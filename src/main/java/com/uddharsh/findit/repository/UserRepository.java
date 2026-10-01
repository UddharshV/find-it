package com.uddharsh.findit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uddharsh.findit.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
