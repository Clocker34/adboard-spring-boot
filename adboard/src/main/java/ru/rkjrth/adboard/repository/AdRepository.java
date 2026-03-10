package ru.rkjrth.adboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rkjrth.adboard.entity.Ad;

public interface AdRepository extends JpaRepository<Ad, Long> {
}
