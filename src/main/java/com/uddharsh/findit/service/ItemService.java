package com.uddharsh.findit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uddharsh.findit.dto.ItemRequest;
import com.uddharsh.findit.entity.Item;
import com.uddharsh.findit.entity.ItemStatus;
import com.uddharsh.findit.entity.User;
import com.uddharsh.findit.exception.ConflictException;
import com.uddharsh.findit.exception.ForbiddenException;
import com.uddharsh.findit.exception.NotFoundException;
import com.uddharsh.findit.repository.ClaimRepository;
import com.uddharsh.findit.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ClaimRepository claimRepository;
    private final UserService userService;

    public ItemService(ItemRepository itemRepository, ClaimRepository claimRepository,
                       UserService userService) {
        this.itemRepository = itemRepository;
        this.claimRepository = claimRepository;
        this.userService = userService;
    }

    @Transactional
    public Item createItem(Long reporterId, ItemRequest request) {
        User reporter = userService.getUser(reporterId);   // throws 404 if the user doesn't exist
        Item item = new Item(request.title(), request.description(), request.type(),
                request.category(), request.location(), request.eventDate(), reporter);
        item.setImageUrl(request.imageUrl());
        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found: " + id));
    }

    @Transactional
    public Item updateItem(Long itemId, Long userId, ItemRequest request) {
        Item item = getItem(itemId);
        requireReporter(item, userId);
        requireStatus(item, ItemStatus.OPEN, "Only open items can be edited");

        item.setTitle(request.title());
        item.setDescription(request.description());
        item.setCategory(request.category());
        item.setLocation(request.location());
        item.setEventDate(request.eventDate());
        item.setImageUrl(request.imageUrl());
        return item;   // no save() needed (see below)
    }

    @Transactional
    public void deleteItem(Long itemId, Long userId) {
        Item item = getItem(itemId);
        requireReporter(item, userId);
        requireStatus(item, ItemStatus.OPEN, "Only open items can be deleted");
        if (claimRepository.existsByItemId(itemId)) {
            throw new ConflictException("Cannot delete an item that has claims");
        }
        itemRepository.delete(item);
    }

    @Transactional
    public Item markReturned(Long itemId, Long userId) {
        Item item = getItem(itemId);
        requireReporter(item, userId);
        requireStatus(item, ItemStatus.CLAIMED, "Only claimed items can be marked returned");
        item.setStatus(ItemStatus.RETURNED);
        return item;
    }

    // --- helper checks ---

    void requireReporter(Item item, Long userId) {
        if (!item.getReportedBy().getId().equals(userId)) {
            throw new ForbiddenException("Only the reporter can change this item");
        }
    }

    private void requireStatus(Item item, ItemStatus expected, String message) {
        if (item.getStatus() != expected) {
            throw new ConflictException(message);
        }
    }
}