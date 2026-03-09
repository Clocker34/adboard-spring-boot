package ru.rkjrth.adboard.controller;

import ru.rkjrth.adboard.dto.MessageDto;
import ru.rkjrth.adboard.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageDto> getAll() { return messageService.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getById(@PathVariable Long id) {
        MessageDto message = messageService.getById(id);
        return message != null ? ResponseEntity.ok(message) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public MessageDto create(@RequestBody MessageDto message) { return messageService.create(message); }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable Long id, @RequestBody MessageDto message) {
        MessageDto updated = messageService.update(id, message);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = messageService.delete(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
