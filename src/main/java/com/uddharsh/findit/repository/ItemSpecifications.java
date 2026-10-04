package com.uddharsh.findit.repository;

import org.springframework.data.jpa.domain.Specification;

import com.uddharsh.findit.entity.Category;
import com.uddharsh.findit.entity.Item;
import com.uddharsh.findit.entity.ItemStatus;
import com.uddharsh.findit.entity.ItemType;

public final class ItemSpecifications {

    private ItemSpecifications() {}   // only static methods, never instantiated

    public static Specification<Item> hasType(ItemType type) {
        return (root, query, cb) -> cb.equal(root.get("type"), type);
    }

    public static Specification<Item> hasStatus(ItemStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Item> hasCategory(Category category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    // Case-insensitive match in the title OR the description
    public static Specification<Item> matchesText(String text) {
        String pattern = "%" + text.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.<String>get("title")), pattern),
                cb.like(cb.lower(root.<String>get("description")), pattern));
    }
}