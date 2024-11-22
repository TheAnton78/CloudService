package ru.netology.cloudservice.repositorytest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository; // Создаем мок для репозитория

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setAuthToken("testToken");
        userRepository.save(user);
    }

    @Test
    public void testFindByUsername() {
        User user = userRepository.findByUsername("testUser");
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void testFindByAuthToken() {
        User authToken = userRepository.findByAuthToken("testToken");
        assertNotNull(authToken);
        assertEquals("testToken", user.getAuthToken());
    }
}
