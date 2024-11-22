package ru.netology.cloudservice.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger LOG = LogManager.getLogger(FileService.class);

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }


    public List<FileInfo> listFiles(int limit, User userId) {
        LOG.info("call listFiles");
        List<FileInfo> fileInfoList = new ArrayList<>();
        fileRepository.findAllByUserId(userId).stream().limit(limit).forEach(file -> {
            fileInfoList.add(new FileInfo(file.getFileName(), file.getFileData().length()));
        });
        return fileInfoList;

    }

    public FileEntity uploadFile(File file, String filename, User userId) throws IOException {
        LOG.info("call uploadFile");
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(filename);
        fileEntity.setFileData(file.getFile());
        fileEntity.setHash(file.getHash());
        fileEntity.setUserId(userId);
        return fileRepository.save(fileEntity);
    }

    public void deleteFile(String filename, User userId) throws Exception {
        LOG.info("call deleteFile");
        fileRepository.delete(fileRepository.findByFileNameAndUserId(filename, userId));
    }


    public boolean containsFile(String filename, User userId) {
        LOG.info("call containsFile");
        return fileRepository.findByFileNameAndUserId(filename, userId) != null;
    }

    public void renameFile(String oldName, String newName, User userId) {
        LOG.info("call renameFile");
        FileEntity newFile = fileRepository.findByFileNameAndUserId(oldName, userId);
        newFile.setFileName(newName);
        fileRepository.save(newFile);
    }


    public FileEntity getFile(String filename, User userId) {
        LOG.info("call getFile");
        return fileRepository.findByFileNameAndUserId(filename, userId);
    }

}
