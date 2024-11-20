package ru.netology.cloudservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    @JsonProperty("hash")
    private String hash;

    @JsonProperty("file")
    private byte[] file;
}

