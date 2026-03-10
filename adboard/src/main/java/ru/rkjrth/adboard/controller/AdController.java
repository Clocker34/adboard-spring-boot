package ru.rkjrth.adboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rkjrth.adboard.entity.Ad;
import ru.rkjrth.adboard.repository.AdRepository;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ads")
public class AdController {

    private final AdRepository adRepository;

    public AdController(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    // Получить все объявления
    @GetMapping
    public List<Ad> getAll() {
        return adRepository.findAll();
    }

    // Получить одно объявление по id
    @GetMapping("/{id}")
    public ResponseEntity<Ad> getById(@PathVariable Long id) {
        return adRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создать объявление
    @PostMapping
    public ResponseEntity<Ad> create(@RequestBody Ad ad) {
        // createdAt можно выставить тут, если не прилетает с клиента
        if (ad.getCreatedAt() == null) {
            ad.setCreatedAt(java.time.LocalDateTime.now());
        }
        Ad saved = adRepository.save(ad);
        return ResponseEntity
                .created(URI.create("/api/ads/" + saved.getId()))
                .body(saved);
    }

    // Обновить объявление
    @PutMapping("/{id}")
    public ResponseEntity<Ad> update(@PathVariable Long id, @RequestBody Ad updated) {
        return adRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(updated.getTitle());
                    existing.setDescription(updated.getDescription());
                    existing.setPrice(updated.getPrice());
                    Ad saved = adRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Удалить объявление
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!adRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        adRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
