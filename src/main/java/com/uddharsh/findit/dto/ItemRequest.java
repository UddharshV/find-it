package com.uddharsh.findit.dto;

import java.time.LocalDate;

import com.uddharsh.findit.entity.Category;
import com.uddharsh.findit.entity.ItemType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record ItemRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 3000) String description,          // optional
        @NotNull ItemType type,
        @NotNull Category category,
        @NotBlank @Size(max = 255) String location,
        @NotNull @PastOrPresent LocalDate eventDate,
        @Size(max = 255) String imageUrl) {}           // optional