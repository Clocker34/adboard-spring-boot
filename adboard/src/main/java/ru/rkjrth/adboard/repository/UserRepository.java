package ru.rkjrth.adboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rkjrth.adboard.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
