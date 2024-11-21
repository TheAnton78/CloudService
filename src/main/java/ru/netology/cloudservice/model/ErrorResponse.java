package ru.netology.cloudservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data

@NoArgsConstructor
public class ErrorResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("id")
    private int id;

    public ErrorResponse(String message, int id) {
        this.message = message;
        this.id = id;
    }


}