package ru.netology.cloudservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileUploadResponse {
    private String message;

    public FileUploadResponse(String message) {
        this.message = message;
    }
}