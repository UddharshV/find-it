package com.uddharsh.findit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.uddharsh.findit.dto.ItemRequest;
import com.uddharsh.findit.dto.ItemResponse;
import com.uddharsh.findit.service.ItemService;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@RequestHeader("X-User-Id") Long userId,
                                   @RequestBody ItemRequest request) {
        return ItemResponse.from(itemService.createItem(userId, request));
    }

    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        return ItemResponse.from(itemService.getItem(id));
    }

    @PutMapping("/{id}")
    public ItemResponse updateItem(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable Long id,
                                   @RequestBody ItemRequest request) {
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