package com.uddharsh.findit.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.uddharsh.findit.dto.ClaimResponse;
import com.uddharsh.findit.dto.CreateClaimRequest;
import com.uddharsh.findit.service.ClaimService;

@RestController
@RequestMapping("/api")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/items/{itemId}/claims")
    @ResponseStatus(HttpStatus.CREATED)
    public ClaimResponse createClaim(@RequestHeader("X-User-Id") Long userId,
                                     @PathVariable Long itemId,
                                     @RequestBody CreateClaimRequest request) {
        return ClaimResponse.from(claimService.createClaim(itemId, userId, request.message()));
    }

    @GetMapping("/items/{itemId}/claims")
    public List<ClaimResponse> getClaims(@RequestHeader("X-User-Id") Long userId,
                                         @PathVariable Long itemId) {
        return claimService.getClaimsForItem(itemId, userId).stream()
                .map(ClaimResponse::from)
                .toList();
    }

    @PostMapping("/claims/{id}/approve")
    public ClaimResponse approveClaim(@RequestHeader("X-User-Id") Long userId,
                                      @PathVariable Long id) {
        return ClaimResponse.from(claimService.approveClaim(id, userId));
    }

    @PostMapping("/claims/{id}/reject")
    public ClaimResponse rejectClaim(@RequestHeader("X-User-Id") Long userId,
                                     @PathVariable Long id) {
        return ClaimResponse.from(claimService.rejectClaim(id, userId));
    }
}