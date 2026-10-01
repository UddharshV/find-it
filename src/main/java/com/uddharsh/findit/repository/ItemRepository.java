package com.uddharsh.findit.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.uddharsh.findit.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
