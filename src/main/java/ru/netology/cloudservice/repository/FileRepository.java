package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.User;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFileName(String name);

    FileEntity findByIdAndUserId(long id, User userId);

    FileEntity findByFileNameAndUserId(String fileName, User userId);

    List<FileEntity> findAllByUserId(User userId);

    void delete(FileEntity file);

}
