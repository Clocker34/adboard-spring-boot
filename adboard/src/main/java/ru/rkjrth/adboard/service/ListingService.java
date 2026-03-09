package ru.rkjrth.adboard.service;

import ru.rkjrth.adboard.dto.ListingDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ListingService {
    private final Map<Long, ListingDto> listings = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<ListingDto> getAll() { return new ArrayList<>(listings.values()); }

    public ListingDto getById(Long id) { return listings.get(id); }

    public ListingDto create(ListingDto listing) {
        Long id = idGenerator.getAndIncrement();
        ListingDto newListing = new ListingDto(id, listing.title(), listing.description(),
                listing.price(), listing.authorId(), listing.categoryId(), listing.status());
        listings.put(id, newListing);
        return newListing;
    }

    public ListingDto update(Long id, ListingDto listing) {
        if (!listings.containsKey(id)) return null;
        ListingDto updated = new ListingDto(id, listing.title(), listing.description(),
                listing.price(), listing.authorId(), listing.categoryId(), listing.status());
        listings.put(id, updated);
        return updated;
    }

    public boolean delete(Long id) { return listings.remove(id) != null; }
}
