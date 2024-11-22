package ru.netology.cloudservice.repositorytest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestFileRepository {

    private final User user = new User(1L, "passN", "passP", "pass");
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private UserRepository userRepository;
    private FileEntity fileEntity;

    @BeforeEach
    public void setUp() {
        fileRepository.deleteAll();
        fileEntity = new FileEntity();
        fileEntity.setFileName("testFileRepository.txt");
        fileEntity.setFileData("example");
        fileEntity.setUserId(user);
        fileRepository.save(fileEntity);
    }


    @AfterEach
    public void tearDown() {
        fileRepository.delete(fileEntity);
    }


    @Test
    public void testFindByFileName() {
        FileEntity foundFile = fileRepository.findByFileNameAndUserId("testFileRepository.txt", user);

        assertNotNull(foundFile);
        assertEquals("testFileRepository.txt", foundFile.getFileName());
    }


    @Test
    public void testFindById() {

        FileEntity foundFile = fileRepository.findByIdAndUserId(fileRepository.findByFileName
                (fileEntity.getFileName()).getId().longValue(), fileEntity.getUserId());
        assertNotNull(foundFile);
        assertEquals("testFileRepository.txt", foundFile.getFileName());
    }

    @Test
    public void testDelete() {

        FileEntity fileEntity = fileRepository.findByFileNameAndUserId("testFileRepository.txt", user);
        assertThat(fileEntity).isNotNull();
        fileRepository.delete(fileEntity);
        assertThat(fileRepository.findByFileName("testFileRepository.txt")).isNull();
    }
}
