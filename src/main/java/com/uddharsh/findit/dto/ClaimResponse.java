package com.uddharsh.findit.dto;

import java.time.Instant;
import com.uddharsh.findit.entity.Claim;
import com.uddharsh.findit.entity.ClaimStatus;

public record ClaimResponse(
        Long id,
        Long itemId,
        Long claimantId,
        String message,
        ClaimStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static ClaimResponse from(Claim claim) {
        return new ClaimResponse(
                claim.getId(), claim.getItem().getId(), claim.getClaimant().getId(),
                claim.getMessage(), claim.getStatus(),
                claim.getCreatedAt(), claim.getUpdatedAt());
    }
}