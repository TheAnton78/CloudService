package ru.netology.cloudservice.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.FileInfo;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;


    @InjectMocks
    private FileService fileService;

    private final User user = new User(13L, "passName", "passPassword", "pass");


    @Test
    public void testListFiles() {
        // Создаем тестовые данные
        FileEntity file1 = new FileEntity();
        file1.setFileName("file1.txt");
        file1.setFileData(("data1"));
        file1.setUserId(user);

        FileEntity file2 = new FileEntity();
        file2.setFileName("file2.txt");
        file2.setFileData("data2");
        file1.setUserId(user);

        List<FileEntity> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        when(fileRepository.findAllByUserId(user)).thenReturn(files);

        List<FileInfo> result = fileService.listFiles(2, user);

        assertEquals(2, result.size());
        assertEquals("file1.txt", result.get(0).getFileName());
        assertEquals("file2.txt", result.get(1).getFileName());
    }

    @Test
    public void testUploadFile() throws IOException {

        String filename = "newfile.txt";
        String fileData = "test data";
        String fileHash = "dummyhash";


        FileEntity mockFileEntity = new FileEntity();
        mockFileEntity.setFileName(filename);
        mockFileEntity.setFileData(fileData);
        mockFileEntity.setHash(fileHash);
        mockFileEntity.setUserId(user);
        System.out.println(mockFileEntity);

        when(fileRepository.save(mockFileEntity)).thenReturn(mockFileEntity);


        FileEntity result = fileService.uploadFile(new File(fileHash, fileData), filename, user);
        System.out.println(result);
        assertNotNull(result);
        assertEquals(filename, result.getFileName());
        verify(fileRepository).save(any(FileEntity.class));
    }

    @Test
    public void testDeleteFile() throws Exception {
        // Создаем тестовые данные
        String filename = "fileToDelete.txt";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(filename);
        fileEntity.setUserId(user);

        when(fileRepository.findByFileNameAndUserId(filename, user)).thenReturn(fileEntity);

        // Тестируем метод
        fileService.deleteFile(filename, user);

        verify(fileRepository).delete(fileEntity);
    }

    @Test
    public void testContainsFile() {
        // Создаем тестовые данные
        String filename = "existingFile.txt";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(filename);
        fileEntity.setUserId(user);

        when(fileRepository.findByFileNameAndUserId(filename, user)).thenReturn(fileEntity);

        // Тестируем метод
        boolean result = fileService.containsFile(filename, user);

        assertTrue(result);
    }

    @Test
    public void testRenameFile() {
        // Создаем тестовые данные
        String oldName = "oldFileName.txt";
        String newName = "newFileName.txt";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(oldName);
        fileEntity.setUserId(user);

        when(fileRepository.findByFileNameAndUserId(oldName, user)).thenReturn(fileEntity);

        // Тестируем метод
        fileService.renameFile(oldName, newName, user);

        assertEquals(newName, fileEntity.getFileName());
        verify(fileRepository).save(fileEntity);
    }

    @Test
    public void testGetFile() {
        // Создаем тестовые данные
        String filename = "fileToGet.txt";
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(filename);
        fileEntity.setUserId(user);

        when(fileRepository.findByFileNameAndUserId(filename, user)).thenReturn(fileEntity);

        // Тестируем метод
        FileEntity result = fileService.getFile(filename, user);

        assertNotNull(result);
        assertEquals(filename, result.getFileName());
    }
}
