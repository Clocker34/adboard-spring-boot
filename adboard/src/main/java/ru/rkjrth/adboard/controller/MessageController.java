package ru.rkjrth.adboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rkjrth.adboard.entity.Message;
import ru.rkjrth.adboard.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    public static class SendMessageRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    // Бизнес-операция 2: отправить сообщение владельцу объявления
    @PostMapping("/listing/{listingId}/from/{senderId}")
    public ResponseEntity<Message> sendMessage(@PathVariable Long listingId,
                                               @PathVariable Long senderId,
                                               @RequestBody SendMessageRequest body) {
        Message message = messageService.sendMessageToListingOwner(listingId, senderId, body.getContent());
        return ResponseEntity.ok(message);
    }
}
