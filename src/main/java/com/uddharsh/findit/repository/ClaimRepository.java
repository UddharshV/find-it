package com.uddharsh.findit.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uddharsh.findit.entity.Claim;
import com.uddharsh.findit.entity.ClaimStatus;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    boolean existsByItemId(Long itemId);
    boolean existsByItemIdAndClaimantId(Long itemId, Long claimantId);
    List<Claim> findByItemId(Long itemId);
    List<Claim> findByItemIdAndStatus(Long itemId, ClaimStatus status);
}
