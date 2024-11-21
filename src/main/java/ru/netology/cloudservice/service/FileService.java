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
import java.util.Objects;

@Service
public class FileService {

    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }


    public List<FileInfo> listFiles(int limit, User userId) {
        List<FileInfo> fileInfoList = new ArrayList<>();

        fileRepository.findAll().stream().filter(file -> Objects.equals(file.getUserId(), userId))
                .map(file -> fileInfoList.add(new FileInfo(file.getFileName(), file.getFileData().length())))
                .close();
        return fileInfoList;
//        FileEntity fileEntity = new FileEntity();
//        System.out.println(repositoryFiles);
//        for (FileEntity repositoryFile : repositoryFiles) {
//            if (repositoryFile.getUserId().equals(userId)) {
//                System.out.println();
//                FileInfo fileInfo = new FileInfo(fileEntity.getFileName(), fileEntity.getFileData().length());
//                fileInfoList.add(fileInfo);
//            } else {
//                return fileInfoList;
//            }
//        }
//        System.out.println(fileInfoList);
//        return fileInfoList;
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
        System.out.println(userId);
        System.out.println(fileRepository.findByIdAndUserId(18, userId));
        return fileRepository.findByFileNameAndUserId(filename, userId);
    }

}
