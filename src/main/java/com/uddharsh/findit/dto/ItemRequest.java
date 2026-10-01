package com.uddharsh.findit.dto;

import java.time.LocalDate;

import com.uddharsh.findit.entity.Category;
import com.uddharsh.findit.entity.ItemType;

public record ItemRequest(
        String title,
        String description,
        ItemType type,
        Category category,
        String location,
        LocalDate eventDate,
        String imageUrl) {}