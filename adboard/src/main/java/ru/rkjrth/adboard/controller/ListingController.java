package ru.rkjrth.adboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rkjrth.adboard.entity.Listing;
import ru.rkjrth.adboard.repository.ListingRepository;
import ru.rkjrth.adboard.service.ListingService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingRepository listingRepository;
    private final ListingService listingService;

    public ListingController(ListingRepository listingRepository,
                             ListingService listingService) {
        this.listingRepository = listingRepository;
        this.listingService = listingService;
    }

    // ==== ФИЛЬТРЫ /search ====

    @GetMapping("/search")
    public List<Listing> searchListings(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Listing.Status status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        // 1) Только categoryId
        if (categoryId != null && ownerId == null && status == null
                && minPrice == null && maxPrice == null) {
            return listingRepository.findByCategory_Id(categoryId);
        }

        // 2) Только ownerId
        if (ownerId != null && categoryId == null && status == null
                && minPrice == null && maxPrice == null) {
            return listingRepository.findByOwner_Id(ownerId);
        }

        // 3) Только статус
        if (status != null && categoryId == null && ownerId == null
                && minPrice == null && maxPrice == null) {
            return listingRepository.findByStatus(status);
        }

        // 4) Только диапазон цены
        if (minPrice != null && maxPrice != null
                && categoryId == null && ownerId == null && status == null) {
            return listingRepository.findByPriceBetween(minPrice, maxPrice);
        }

        // 5) Категория + статус
        if (categoryId != null && status != null
                && ownerId == null && minPrice == null && maxPrice == null) {
            return listingRepository.findByCategory_IdAndStatus(categoryId, status);
        }

        // Во всех других случаях пока отдаём все объявления
        return listingRepository.findAll();
    }

    // ==== Обычный CRUD ====

    @GetMapping
    public List<Listing> getAll() {
        return listingRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Listing> getById(@PathVariable Long id) {
        return listingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Listing> create(@RequestBody Listing listing) {
        if (listing.getCreatedAt() == null) {
            listing.setCreatedAt(java.time.LocalDateTime.now());
        }
        if (listing.getStatus() == null) {
            listing.setStatus(Listing.Status.ACTIVE);
        }
        Listing saved = listingRepository.save(listing);
        return ResponseEntity
                .created(URI.create("/api/listings/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Listing> update(@PathVariable Long id,
                                          @RequestBody Listing updated) {
        return listingRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());
                    existing.setPrice(updated.getPrice());
                    if (updated.getStatus() != null) {
                        existing.setStatus(updated.getStatus());
                    }
                    Listing saved = listingRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!listingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        listingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==== Бизнес-операция 1: создать объявление от пользователя в категории ====

    @PostMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<Listing> createForUserInCategory(@PathVariable Long userId,
                                                           @PathVariable Long categoryId,
                                                           @RequestBody Listing body) {
        Listing created = listingService.createListingForUserInCategory(
                userId,
                categoryId,
                body.getTitle(),
                body.getDescription(),
                body.getPrice()
        );
        return ResponseEntity
                .created(URI.create("/api/listings/" + created.getId()))
                .body(created);
    }
}
