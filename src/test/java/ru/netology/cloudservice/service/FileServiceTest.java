package ru.netology.cloudservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.FileInfo;
import ru.netology.cloudservice.repository.FileRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    private MultipartFile mockFile;
    private FileEntity fileEntity;

    @BeforeEach
    public void setUp() {
        fileEntity = new FileEntity();
        fileEntity.setFileName("testFileService.txt");
        fileEntity.setFileData("Sample data".getBytes());
        mockFile = new MockMultipartFile("file", "testFileService.txt", "text/plain", "Sample data".getBytes());
    }

    @Test
    public void testListFiles() {

        when(fileRepository.findById(0L)).thenReturn(fileEntity);

        List<FileInfo> fileInfoList = fileService.listFiles(1);

        assertEquals(1, fileInfoList.size());
        assertEquals("testFileService.txt", fileInfoList.get(0).getFileName());
        assertEquals("Sample data".length(), fileInfoList.get(0).getFileSize());
    }

    @Test
    public void testUploadFile() throws IOException {
        when(fileRepository.save(any(FileEntity.class))).thenReturn(fileEntity);

        FileEntity savedFileEntity = fileService.uploadFile(mockFile, "testFileService.txt");

        assertNotNull(savedFileEntity);
        assertEquals("testFileService.txt", savedFileEntity.getFileName());
        verify(fileRepository, times(1)).save(any(FileEntity.class));
    }

    @Test
    public void testDeleteFile() throws Exception {
        when(fileRepository.findByFileName("testFileService.txt")).thenReturn(fileEntity);

        fileService.deleteFile("testFileService.txt");

        verify(fileRepository, times(1)).delete(fileEntity);
    }

    @Test
    public void testContainsFile() {
        when(fileRepository.findByFileName("testFileService.txt")).thenReturn(fileEntity);

        boolean exists = fileService.containsFile("testFileService.txt");

        assertTrue(exists);
    }

    @Test
    public void testRenameFile() {
        when(fileRepository.findByFileName("testFileService.txt")).thenReturn(fileEntity);

        fileService.renameFile("testFileService.txt", "newFile.txt");

        ArgumentCaptor<FileEntity> argumentCaptor = ArgumentCaptor.forClass(FileEntity.class);
        verify(fileRepository, times(1)).save(argumentCaptor.capture());
        assertEquals("newFile.txt", argumentCaptor.getValue().getFileName());
    }

    @Test
    public void testGetFile() {
        when(fileRepository.findByFileName("testFileService.txt")).thenReturn(fileEntity);

        FileEntity retrievedFile = fileService.getFile("testFileService.txt");

        assertNotNull(retrievedFile);
        assertEquals("testFileService.txt", retrievedFile.getFileName());
    }
}
