package com.uddharsh.findit.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.uddharsh.findit.dto.ItemRequest;
import com.uddharsh.findit.dto.ItemResponse;
import com.uddharsh.findit.dto.PageResponse;
import com.uddharsh.findit.entity.ItemStatus;
import com.uddharsh.findit.entity.ItemType;
import com.uddharsh.findit.service.ItemService;

import com.uddharsh.findit.entity.Category;
import com.uddharsh.findit.entity.ItemStatus;
import com.uddharsh.findit.entity.ItemType;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public PageResponse<ItemResponse> listItems(
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return PageResponse.from(
                itemService.searchItems(type, status, category, q, pageable),
                ItemResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody ItemRequest request) {
        return ItemResponse.from(itemService.createItem(userId, request));
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        return ItemResponse.from(itemService.getItem(id));
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable Long id,
                                   @Valid @RequestBody ItemRequest request) {
        return ItemResponse.from(itemService.updateItem(id, userId, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@RequestHeader("X-User-Id") Long userId,
                           @PathVariable Long id) {
        itemService.deleteItem(id, userId);
    }

    @PostMapping("/{id}/return")
    public ItemResponse markReturned(@RequestHeader("X-User-Id") Long userId,
                                     @PathVariable Long id) {
        return ItemResponse.from(itemService.markReturned(id, userId));
    }
}