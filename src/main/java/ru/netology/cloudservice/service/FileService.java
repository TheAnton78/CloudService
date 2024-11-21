package ru.netology.cloudservice.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.FileInfo;
import ru.netology.cloudservice.model.File;
import ru.netology.cloudservice.model.User;
import ru.netology.cloudservice.repository.FileRepository;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }


    public List<FileInfo> listFiles(int limit, User userId) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        int repositoryFiles = fileRepository.findAll().size();
        long fileId = 1;

            while (fileInfoList.size() < limit && fileInfoList.size() < repositoryFiles) {

                FileEntity fileEntity = fileRepository.findByIdAndUserId(fileId, userId);

                if(fileEntity != null) {
                    FileInfo fileInfo = new FileInfo(fileEntity.getFileName(), fileEntity.getFileData().length());
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

    public FileEntity uploadFile(File file, String filename, User userId) throws IOException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(filename);
        fileEntity.setFileData(file.getFile());
        fileEntity.setHash(file.getHash());
        fileEntity.setUserId(userId);
        return fileRepository.save(fileEntity);
    }

    public void deleteFile(String filename, User userId) throws Exception {
        fileRepository.delete(fileRepository.findByFileNameAndUserId(filename, userId));
    }


    public boolean containsFile(String filename, User userId) {
        return fileRepository.findByFileNameAndUserId(filename, userId) != null;
    }

    public void renameFile(String oldName, String newName, User userId) {
        FileEntity newFile = fileRepository.findByFileNameAndUserId(oldName, userId);
        newFile.setFileName(newName);
        fileRepository.save(newFile);
    }


    public FileEntity getFile(String filename, User userId) {
        return fileRepository.findByFileNameAndUserId(filename, userId);
    }

}
