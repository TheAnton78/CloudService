package ru.netology.cloudservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class File {
    @JsonProperty("hash")
    private String hash;

    @JsonProperty("file")
    private String file;





}

