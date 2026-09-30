package com.uddharsh.findit.entity;

import java.time.Instant;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;


@Entity 
@Table(
    name = "claims", 
    uniqueConstraints = @UniqueConstraint(columnNames= {"item_id", "claimant_id"}), //Claim doesn't need an index for item_id because of the unique constraint
    indexes = @Index(name = "idx_claims_claimant", columnList = "claimant_id")
)
public class Claim {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claimant_id")
    private User claimant;

    @Column(nullable = false, length = 1000)
    private String message;             // proof of ownership, e.g. "It has a sticker of..."

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;   // every new claim starts PENDING

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;          // shows when it was approved or rejected

    protected Claim() {}

    public Claim(Item item, User claimant, String message) {
        this.item = item;
        this.claimant = claimant;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public User getClaimant() {
        return claimant;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    
}
