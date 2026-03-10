package ru.rkjrth.adboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rkjrth.adboard.entity.Listing;

import java.math.BigDecimal;
import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByCategory_Id(Long categoryId);

    List<Listing> findByOwner_Id(Long ownerId);

    List<Listing> findByStatus(Listing.Status status);

    List<Listing> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Listing> findByCategory_IdAndStatus(Long categoryId, Listing.Status status);
}
