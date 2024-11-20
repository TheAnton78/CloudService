package ru.netology.cloudservice.service;


import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ru.netology.cloudservice.model.ErrorResponse;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.FileInfo;
import ru.netology.cloudservice.repository.FileRepository;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }


    public List<FileInfo> listFiles(int limit) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        int repositoryFiles = fileRepository.findAll().size();
        long fileId = 1;

            while (fileInfoList.size() < limit && fileInfoList.size() < repositoryFiles) {

                FileEntity fileEntity = fileRepository.findById(fileId);
                if(fileEntity != null) {
                    FileInfo fileInfo = new FileInfo(fileEntity.getFileName(), fileEntity.getFileData().length);
                    fileInfoList.add(fileInfo);
                }
                fileId++;
            }

//            while (fileInfoList.size() < repositoryFiles) {
//                FileEntity fileEntity = fileRepository.findById(fileId);
//                if(fileEntity != null) {
//                    FileInfo fileInfo = new FileInfo(fileEntity.getFileName(), fileEntity.getFileData().length);
//                    fileInfoList.add(fileInfo);
//                }
//                fileId++;
//            }

        return fileInfoList;
    }

    public FileEntity uploadFile(MultipartFile file, String filename) throws IOException {

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(filename);
        fileEntity.setFileData(file.getBytes());
        return fileRepository.save(fileEntity);



    }

    public void deleteFile(String filename) throws Exception {
        fileRepository.delete(fileRepository.findByFileName(filename));
    }


    public boolean containsFile(String filename) {
        return fileRepository.findByFileName(filename) != null;
    }

    public void renameFile(String oldName, String newName) {
        FileEntity newFile = fileRepository.findByFileName(oldName);
        newFile.setFileName(newName);
        fileRepository.save(newFile);
    }


    public FileEntity getFile(String filename) {
        return fileRepository.findByFileName(filename);
    }

}
