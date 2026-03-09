package ru.rkjrth.adboard.controller;

import ru.rkjrth.adboard.dto.ListingDto;
import ru.rkjrth.adboard.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public List<ListingDto> getAll() { return listingService.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<ListingDto> getById(@PathVariable Long id) {
        ListingDto listing = listingService.getById(id);
        return listing != null ? ResponseEntity.ok(listing) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ListingDto create(@RequestBody ListingDto listing) { return listingService.create(listing); }

    @PutMapping("/{id}")
    public ResponseEntity<ListingDto> update(@PathVariable Long id, @RequestBody ListingDto listing) {
        ListingDto updated = listingService.update(id, listing);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = listingService.delete(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
