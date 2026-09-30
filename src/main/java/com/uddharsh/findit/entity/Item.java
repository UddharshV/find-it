package com.uddharsh.findit.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;


@Entity 
@Table(
    name="items",
    indexes = {
        @Index(name = "idx_items_status", columnList = "status"),
        @Index(name = "idx_items_type", columnList = "type"),
        @Index(name = "idx_items_category", columnList = "category"),
        @Index(name = "idx_items_reported_by", columnList = "reported_by_id")
    } //Item list page will filter by type, status, and category
)
public class Item {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000)              // default is 255 characters; descriptions need more
    private String description;

    @Enumerated(EnumType.STRING)        // store "LOST, FOUND", not 0
    @Column(nullable = false)
    private ItemType type;

    @Enumerated(EnumType.STRING)        // store "OPEN, CLAIMED, RETURNED", not 0
    @Column(nullable = false)
    private ItemStatus status = ItemStatus.OPEN; //every new item starts OPEN

    @Enumerated(EnumType.STRING)        // store "ELECTRONICS, ID_CARD, KEYS, CLOTHING, BAGS, BOTTLES, BOOKS, OTHER", not 0
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String location;            // e.g. "Hunt Library, 2nd floor"

    @Column(nullable = false)
    private LocalDate eventDate;        // date it was lost or found (no time needed)

    private String imageUrl;            // optional, so nullable

    @ManyToOne(fetch = FetchType.LAZY, optional = false)   // many items → one user
    @JoinColumn(name = "reported_by_id")                   // the foreign key column
    private User reportedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp                    // refreshed automatically on every change
    @Column(nullable = false)
    private Instant updatedAt;

    protected Item() {}

    public Item(String title, String description, ItemType type, Category category,
                String location, LocalDate eventDate, User reportedBy) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.reportedBy = reportedBy;
    }

    public Long getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}


