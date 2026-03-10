package ru.rkjrth.adboard.service;

import org.springframework.stereotype.Service;
import ru.rkjrth.adboard.entity.Ad;
import ru.rkjrth.adboard.repository.AdRepository;

import java.util.List;

@Service
public class AdService {

    private final AdRepository adRepository;

    public AdService(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    public List<Ad> findAll() {
        return adRepository.findAll();
    }

    public Ad findById(Long id) {
        return adRepository.findById(id).orElse(null);
    }

    public Ad create(Ad ad) {
        return adRepository.save(ad);
    }

    public void delete(Long id) {
        adRepository.deleteById(id);
    }
}
