package com.uddharsh.findit.dto;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

public record PageResponse<T>(
        List<T> content,       // the items on this page
        int page,              // current page number (starts at 0)
        int size,              // items per page
        long totalElements,    // total items across all pages
        int totalPages) {

    // Converts a Page of entities into a PageResponse of DTOs
    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}