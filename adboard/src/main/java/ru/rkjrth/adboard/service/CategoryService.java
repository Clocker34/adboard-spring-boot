package ru.rkjrth.adboard.service;

import ru.rkjrth.adboard.dto.CategoryDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CategoryService {
    private final Map<Long, CategoryDto> categories = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<CategoryDto> getAll() {
        return new ArrayList<>(categories.values());
    }

    public CategoryDto getById(Long id) {
        return categories.get(id);
    }

    public CategoryDto create(CategoryDto category) {
        Long id = idGenerator.getAndIncrement();
        CategoryDto newCategory = new CategoryDto(id, category.name(), category.description());
        categories.put(id, newCategory);
        return newCategory;
    }

    public CategoryDto update(Long id, CategoryDto category) {
        if (!categories.containsKey(id)) return null;
        CategoryDto updated = new CategoryDto(id, category.name(), category.description());
        categories.put(id, updated);
        return updated;
    }

    public boolean delete(Long id) {
        return categories.remove(id) != null;
    }
}
