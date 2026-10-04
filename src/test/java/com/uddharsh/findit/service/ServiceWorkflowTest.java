package com.uddharsh.findit.service;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import com.uddharsh.findit.dto.ItemRequest;
import com.uddharsh.findit.entity.Category;
import com.uddharsh.findit.entity.Claim;
import com.uddharsh.findit.entity.ClaimStatus;
import com.uddharsh.findit.entity.Item;
import com.uddharsh.findit.entity.ItemStatus;
import com.uddharsh.findit.entity.ItemType;
import com.uddharsh.findit.entity.User;
import com.uddharsh.findit.exception.ConflictException;
import com.uddharsh.findit.exception.ForbiddenException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({UserService.class, ItemService.class, ClaimService.class})  // @DataJpaTest doesn't load services by itself
class ServiceWorkflowTest {

    @Autowired UserService userService;
    @Autowired ItemService itemService;
    @Autowired ClaimService claimService;

    // Shared starting point, rebuilt fresh before every test
    private User reporter;
    private User claimant;
    private Item item;

    @BeforeEach
    void setUp() {
        reporter = userService.createUser("Asha", "asha@ncsu.edu");
        claimant = userService.createUser("Ben", "ben@ncsu.edu");
        item = itemService.createItem(reporter.getId(), sampleItem());
    }

    private ItemRequest sampleItem() {
        return new ItemRequest("Water bottle", "Blue, with stickers", ItemType.LOST,
                Category.BOTTLES, "Hunt Library", LocalDate.now(), null);
    }


    @Test
    void approvingClaimMarksItemClaimedAndRejectsOthers() {
        // Arrange: two people claim the same item
        User other = userService.createUser("Chris", "chris@ncsu.edu");
        Claim winner = claimService.createClaim(item.getId(), claimant.getId(), "Has my sticker");
        Claim loser  = claimService.createClaim(item.getId(), other.getId(), "It's mine");

        // Act: the reporter approves one of them
        claimService.approveClaim(winner.getId(), reporter.getId());

        // Assert: all three status changes happened
        assertEquals(ClaimStatus.APPROVED, winner.getStatus());
        assertEquals(ClaimStatus.REJECTED, loser.getStatus());
        assertEquals(ItemStatus.CLAIMED, item.getStatus());
    }

    @Test
    void cannotClaimOwnItem() {
        // reporter tries to claim their own item → assertThrows(ConflictException.class, ...)
        // Arrange: item from setUp()

        // Act and Assert
        assertThrows(ConflictException.class, () ->
    claimService.createClaim(item.getId(), reporter.getId(), "This is my item")
);
    }

    @Test
    void cannotClaimSameItemTwice() {
        // claimant claims once, then again → second attempt throws ConflictException
        claimService.createClaim(item.getId(), claimant.getId(), "Claiming my item");
        assertThrows(ConflictException.class, () -> claimService.createClaim(item.getId(), claimant.getId(), "Claiming my item again!"));
    }

    @Test
    void cannotClaimItemThatIsNotOpen() {
        // approve a claim first (item becomes CLAIMED), then a new user tries to claim → ConflictException
        Claim firstClaim = claimService.createClaim(item.getId(), claimant.getId(), "Claiming my item");
        assertNotNull(firstClaim.getId());

        claimService.approveClaim(firstClaim.getId(), reporter.getId());
        assertEquals(ItemStatus.CLAIMED, item.getStatus());

        User secondClaimant = userService.createUser("Chris", "chris@ncsu.edu");
        assertThrows(ConflictException.class, () ->
    claimService.createClaim(item.getId(), secondClaimant.getId(), "This is my item"));
        
    }

    @Test
    void onlyReporterCanApproveClaim() {
        // claimant tries to approve their own claim → ForbiddenException
    
        Claim claim = claimService.createClaim(item.getId(), claimant.getId(), "Claiming my item");
        assertNotNull(claim.getId());

        assertThrows(ForbiddenException.class, () ->
    claimService.approveClaim(claim.getId(), claimant.getId()));


    }

    @Test
    void cannotApproveClaimThatIsNotPending() {
        // reject a claim, then try to approve it → ConflictException

        Claim claim = claimService.createClaim(item.getId(), claimant.getId(), "Claiming my item");
        assertNotNull(claim.getId());

        Claim rejectedClaim = claimService.rejectClaim(claim.getId(), reporter.getId());
        assertEquals(ClaimStatus.REJECTED, rejectedClaim.getStatus());
        
        assertThrows(ConflictException.class, () ->
    claimService.approveClaim(rejectedClaim.getId(), reporter.getId()));

    }

    @Test
    void onlyReporterCanViewClaims() {
        // claimant calls getClaimsForItem → ForbiddenException
        Claim claim = claimService.createClaim(item.getId(), claimant.getId(), "Claiming my item");
        assertNotNull(claim.getId());

        assertThrows(ForbiddenException.class, () ->
    claimService.getClaimsForItem(item.getId(), claimant.getId()));
        
    }

    @Test
    void markReturnedRequiresClaimedItem() {
        // call markReturned on an OPEN item → ConflictException
        // then approve a claim and call markReturned → status becomes RETURNED

        assertThrows(ConflictException.class, () ->
    itemService.markReturned(item.getId(), reporter.getId()));
        
        Claim claim = claimService.createClaim(item.getId(), claimant.getId(), "Claiming my item");
        assertNotNull(claim.getId());

        claimService.approveClaim(claim.getId(), reporter.getId());
        assertEquals(ItemStatus.CLAIMED, item.getStatus());

        Item returnedItem = itemService.markReturned(item.getId(), reporter.getId());
        assertEquals(ItemStatus.RETURNED, returnedItem.getStatus());


    }

    @Test
    void cannotDeleteItemWithClaims() {
        // file a claim, then reporter tries deleteItem → ConflictException
        Claim claim = claimService.createClaim(item.getId(), claimant.getId(), "Claiming my item");
        assertNotNull(claim.getId());

        assertThrows(ConflictException.class, () ->
    itemService.deleteItem(item.getId(), reporter.getId()));
    }

    @Test
    void duplicateEmailIsRejected() {
        // createUser with "ASHA@ncsu.edu" (different case) → ConflictException
        assertThrows(ConflictException.class, () -> userService.createUser("ASHA", "ASHA@ncsu.edu"));
    }
    
    @Test
    void searchFiltersByTypeAndText() {
        itemService.createItem(reporter.getId(), new ItemRequest("Car keys", "Toyota keychain",
                ItemType.FOUND, Category.KEYS, "Talley", LocalDate.now(), null));
        Pageable firstPage = PageRequest.of(0, 20);

        Page<Item> found = itemService.searchItems(ItemType.FOUND, null, null, null, firstPage);
        assertEquals(1, found.getTotalElements());
        assertEquals("Car keys", found.getContent().get(0).getTitle());

        Page<Item> byText = itemService.searchItems(null, null, null, "KEYCHAIN", firstPage);
        assertEquals(1, byText.getTotalElements());   // matches the description, ignoring case

        Page<Item> all = itemService.searchItems(null, null, null, null, firstPage);
        assertEquals(2, all.getTotalElements());      // no filters → everything
    }
}