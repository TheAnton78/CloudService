package ru.netology.cloudservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "usersdata")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String authToken;

    public User(String username, String password, String authToken) {
        this.username = username;
        this.password = password;
        this.authToken = authToken;
    }

}