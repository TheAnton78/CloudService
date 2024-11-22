package ru.netology.cloudservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    public void setUp() {
        // Устанавливаем пользователя для тестов
        user = new User("testUser", "encodedPassword", "authToken123");
    }

    @Test
    public void testLoadUserExistingUser() {

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        User user = customUserDetailsService.loadUser("testUser");


        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }


    @Test
    public void testUploadUserNewUser() {

        when(userRepository.findByUsername("newUser")).thenReturn(null);


        customUserDetailsService.uploadUser(new User("newUser", "password", "authToken456"));


        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUploadUserExistingUser() {

        when(userRepository.findByUsername("existingUser")).thenReturn(user);


        assertThrows(RuntimeException.class, () -> {
            customUserDetailsService.uploadUser(new User("existingUser", "password", "authToken456"));
        });
    }

    @Test
    public void testContainsUsernameTrue() {

        when(userRepository.findByUsername("existingUser")).thenReturn(user);


        assertTrue(customUserDetailsService.containsUsername("existingUser"));
    }

    @Test
    public void testContainsUsernameFalse() {

        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);


        assertFalse(customUserDetailsService.containsUsername("nonExistingUser"));
    }

    @Test
    public void testUncontainsAuthTokenTrue() {
        when(userRepository.findByAuthToken("invalidToken")).thenReturn(null);
        assertTrue(customUserDetailsService.uncontainsAuthToken("invalidToken"));
    }

    @Test
    public void testUncontainsAuthTokenFalse() {
        when(userRepository.findByAuthToken("validToken")).thenReturn(user);
        assertFalse(customUserDetailsService.uncontainsAuthToken("validToken"));
    }
}
