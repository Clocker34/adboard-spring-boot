package ru.rkjrth.adboard.service;

import ru.rkjrth.adboard.dto.MessageDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {
    private final Map<Long, MessageDto> messages = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<MessageDto> getAll() { return new ArrayList<>(messages.values()); }

    public MessageDto getById(Long id) { return messages.get(id); }

    public MessageDto create(MessageDto message) {
        Long id = idGenerator.getAndIncrement();
        MessageDto newMessage = new MessageDto(id, message.text(), message.senderId(),
                message.recipientId(), message.listingId());
        messages.put(id, newMessage);
        return newMessage;
    }

    public MessageDto update(Long id, MessageDto message) {
        if (!messages.containsKey(id)) return null;
        MessageDto updated = new MessageDto(id, message.text(), message.senderId(),
                message.recipientId(), message.listingId());
        messages.put(id, updated);
        return updated;
    }

    public boolean delete(Long id) { return messages.remove(id) != null; }
}
