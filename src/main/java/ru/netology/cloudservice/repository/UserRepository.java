package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.cloudservice.model.User;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByAuthToken(String authToken);
}
