package com.uddharsh.findit.dto;

import java.time.Instant;
import java.time.LocalDate;
import com.uddharsh.findit.entity.*;

public record ItemResponse(
        Long id,
        String title,
        String description,
        ItemType type,
        ItemStatus status,
        Category category,
        String location,
        LocalDate eventDate,
        String imageUrl,
        Long reporterId,
        Instant createdAt,
        Instant updatedAt) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(), item.getTitle(), item.getDescription(),
                item.getType(), item.getStatus(), item.getCategory(),
                item.getLocation(), item.getEventDate(), item.getImageUrl(),
                item.getReportedBy().getId(),
                item.getCreatedAt(), item.getUpdatedAt());
    }
}