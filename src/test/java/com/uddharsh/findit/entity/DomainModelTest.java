package com.uddharsh.findit.entity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.dao.DataIntegrityViolationException;

import com.uddharsh.findit.repository.ClaimRepository;
import com.uddharsh.findit.repository.ItemRepository;
import com.uddharsh.findit.repository.UserRepository;

@DataJpaTest                                        // loads only the database parts of the app
@AutoConfigureTestDatabase(replace = Replace.NONE)  // use your real Postgres, not an in-memory DB
class DomainModelTest {

    @Autowired UserRepository userRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired ClaimRepository claimRepository;

    @Test
    void savesUserWithNormalizedEmail() {
        User saved = userRepository.saveAndFlush(new User("Asha", "  Asha@NCSU.edu "));

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("asha@ncsu.edu", saved.getEmail());
    }

    @Test
    void savesItemLinkedToUser() {
        User user = userRepository.saveAndFlush(new User("Asha", "asha@ncsu.edu"));
        Item item = itemRepository.saveAndFlush(new Item(
                "Water bottle", "Blue, with stickers", ItemType.LOST, Category.BOTTLES,
                "Hunt Library", LocalDate.now(), user));

        assertNotNull(item.getId());
        assertEquals(ItemStatus.OPEN, item.getStatus());          // default status
        assertEquals(user.getId(), item.getReportedBy().getId()); // relationship works
    }

    @Test
    void rejectsDuplicateClaimBySameUser() {
        User reporter = userRepository.saveAndFlush(new User("Asha", "asha@ncsu.edu"));
        User claimant = userRepository.saveAndFlush(new User("Ben", "ben@ncsu.edu"));
        Item item = itemRepository.saveAndFlush(new Item(
                "Keys", null, ItemType.FOUND, Category.KEYS,
                "Talley", LocalDate.now(), reporter));

        claimRepository.saveAndFlush(new Claim(item, claimant, "Red keychain"));

        var ex = assertThrows(DataIntegrityViolationException.class, () ->
        claimRepository.saveAndFlush(new Claim(item, claimant, "Trying again")));

        assertNotNull(ex.getMessage());
    }
}
