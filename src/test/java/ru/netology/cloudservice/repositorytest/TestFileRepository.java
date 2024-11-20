package ru.netology.cloudservice.repositorytest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.repository.FileRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestFileRepository {

    @Autowired
    private FileRepository fileRepository;

    private FileEntity fileEntity;

    @BeforeEach
    public void setUp() {
        fileEntity = new FileEntity();
        fileEntity.setId(1L);
        fileEntity.setFileName("testFileRepository.txt");
        fileEntity.setFileData("example".getBytes());
        fileRepository.save(fileEntity);
    }

    @AfterEach
    public void tearDown() {
        fileRepository.delete(fileEntity);
    }


    @Test
    public void testFindByFileName() {
        FileEntity foundFile = fileRepository.findByFileName("testFileRepository.txt");
        assertNotNull(foundFile);
        assertEquals("testFileRepository.txt", foundFile.getFileName());
    }


    @Test
    public void testFindById() {
        FileEntity foundFile = fileRepository.findById(1L);
        assert foundFile != null;
        assertEquals("testFileRepository.txt", foundFile.getFileName());
    }

    @Test
    public void testDelete() {

        FileEntity fileEntity = fileRepository.findByFileName("testFileRepository.txt");
        assertThat(fileEntity).isNotNull();
        fileRepository.delete(fileEntity);
        assertThat(fileRepository.findByFileName("testFileRepository.txt")).isNull();
    }
}
