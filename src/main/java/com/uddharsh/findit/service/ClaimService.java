package com.uddharsh.findit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uddharsh.findit.entity.Claim;
import com.uddharsh.findit.entity.ClaimStatus;
import com.uddharsh.findit.entity.Item;
import com.uddharsh.findit.entity.ItemStatus;
import com.uddharsh.findit.entity.User;
import com.uddharsh.findit.exception.ConflictException;
import com.uddharsh.findit.exception.NotFoundException;
import com.uddharsh.findit.repository.ClaimRepository;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemService itemService;
    private final UserService userService;

    public ClaimService(ClaimRepository claimRepository, ItemService itemService,
                        UserService userService) {
        this.claimRepository = claimRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Transactional
    public Claim createClaim(Long itemId, Long claimantId, String message) {
        Item item = itemService.getItem(itemId);
        User claimant = userService.getUser(claimantId);

        if (item.getStatus() != ItemStatus.OPEN) {
            throw new ConflictException("Only open items can be claimed");
        }
        if (item.getReportedBy().getId().equals(claimantId)) {
            throw new ConflictException("You cannot claim your own item");
        }
        if (claimRepository.existsByItemIdAndClaimantId(itemId, claimantId)) {
            throw new ConflictException("You have already claimed this item");
        }
        return claimRepository.save(new Claim(item, claimant, message));
    }

    @Transactional(readOnly = true)
    public List<Claim> getClaimsForItem(Long itemId, Long userId) {
        Item item = itemService.getItem(itemId);
        itemService.requireReporter(item, userId);
        return claimRepository.findByItemId(itemId);
    }

    @Transactional
    public Claim approveClaim(Long claimId, Long userId) {
        Claim claim = getPendingClaim(claimId);
        Item item = claim.getItem();
        itemService.requireReporter(item, userId);

        if (item.getStatus() != ItemStatus.OPEN) {
            throw new ConflictException("This item is no longer open");
        }

        claim.setStatus(ClaimStatus.APPROVED);
        item.setStatus(ItemStatus.CLAIMED);

        // reject everyone else who was waiting
        for (Claim other : claimRepository.findByItemIdAndStatus(item.getId(), ClaimStatus.PENDING)) {
            if (!other.getId().equals(claim.getId())) {
                other.setStatus(ClaimStatus.REJECTED);
            }
        }
        return claim;
    }

    @Transactional
    public Claim rejectClaim(Long claimId, Long userId) {
        Claim claim = getPendingClaim(claimId);
        itemService.requireReporter(claim.getItem(), userId);
        claim.setStatus(ClaimStatus.REJECTED);
        return claim;
    }

    // --- helper ---

    private Claim getPendingClaim(Long claimId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new NotFoundException("Claim not found: " + claimId));
        if (claim.getStatus() != ClaimStatus.PENDING) {
            throw new ConflictException("Only pending claims can be approved or rejected");
        }
        return claim;
    }
}