package ru.rkjrth.adboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rkjrth.adboard.entity.Listing;
import ru.rkjrth.adboard.entity.Message;
import ru.rkjrth.adboard.entity.User;
import ru.rkjrth.adboard.repository.ListingRepository;
import ru.rkjrth.adboard.repository.MessageRepository;
import ru.rkjrth.adboard.repository.UserRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          ListingRepository listingRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    /**
     * Бизнес-операция 2:
     * Отправить сообщение по объявлению от sender к владельцу объявления.
     */
    @Transactional
    public Message sendMessageToListingOwner(Long listingId,
                                             Long senderId,
                                             String content) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found: " + listingId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderId));

        User recipient = listing.getOwner();
        if (recipient.getId().equals(sender.getId())) {
            throw new IllegalArgumentException("Owner cannot send message to himself for this listing");
        }

        Message message = new Message(content, sender, recipient, listing);
        return messageRepository.save(message);
    }
}
