package ru.rkjrth.adboard.service;

import ru.rkjrth.adboard.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final Map<Long, UserDto> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<UserDto> getAll() { return new ArrayList<>(users.values()); }

    public UserDto getById(Long id) { return users.get(id); }

    public UserDto create(UserDto user) {
        Long id = idGenerator.getAndIncrement();
        UserDto newUser = new UserDto(id, user.name(), user.email());
        users.put(id, newUser);
        return newUser;
    }

    public UserDto update(Long id, UserDto user) {
        if (!users.containsKey(id)) return null;
        UserDto updated = new UserDto(id, user.name(), user.email());
        users.put(id, updated);
        return updated;
    }

    public boolean delete(Long id) { return users.remove(id) != null; }
}
