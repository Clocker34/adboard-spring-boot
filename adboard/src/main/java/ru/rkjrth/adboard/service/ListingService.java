package ru.rkjrth.adboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rkjrth.adboard.entity.Category;
import ru.rkjrth.adboard.entity.Listing;
import ru.rkjrth.adboard.entity.User;
import ru.rkjrth.adboard.repository.CategoryRepository;
import ru.rkjrth.adboard.repository.ListingRepository;
import ru.rkjrth.adboard.repository.UserRepository;

import java.math.BigDecimal;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ListingService(ListingRepository listingRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Бизнес-операция 1:
     * Создать объявление для пользователя в заданной категории.
     */
    @Transactional
    public Listing createListingForUserInCategory(Long userId,
                                                  Long categoryId,
                                                  String title,
                                                  String description,
                                                  BigDecimal price) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));

        Listing listing = new Listing(title, description, price, owner, category);
        return listingRepository.save(listing);
    }
}
