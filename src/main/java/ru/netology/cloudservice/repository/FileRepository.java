package ru.netology.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudservice.model.FileEntity;
import ru.netology.cloudservice.model.User;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFileName(String name);

    FileEntity findByIdAndUserId(long id, User userId);

    FileEntity findByFileNameAndUserId(String fileName, User userId);

    FileEntity findById(long id);

    void delete(FileEntity file);

}
