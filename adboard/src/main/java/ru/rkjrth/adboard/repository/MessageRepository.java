package ru.rkjrth.adboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rkjrth.adboard.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
