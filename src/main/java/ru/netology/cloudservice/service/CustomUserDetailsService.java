package ru.netology.cloudservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;

@Service
public class CustomUserDetailsService {
    @Autowired
    private UserRepository userRepository;



    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User loadUser(String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }


    public void uploadUser(String username, String password, String authToken) {
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("User already exists");
        }
        User user = new User(username, passwordEncoder.encode(password), authToken);
        userRepository.save(user);
    }

    public boolean containsUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }
    public boolean uncontainsAuthToken(String authToken) {
        return userRepository.findByAuthToken(authToken) == null;
    }

    public void updateAuthToken(String oldAuthToken, String newAuthToken) {
        User user = userRepository.findByAuthToken(oldAuthToken);
        user.setAuthToken(newAuthToken);
        userRepository.save(user);
    }

    public User getUserByAuthToken(String authToken) {
        return userRepository.findByAuthToken(authToken);
    }
}
