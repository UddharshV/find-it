package com.uddharsh.findit.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.uddharsh.findit.entity.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {}
