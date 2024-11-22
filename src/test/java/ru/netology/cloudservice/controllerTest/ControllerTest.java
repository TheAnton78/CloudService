package ru.netology.cloudservice.controllerTest;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import ru.netology.cloudservice.model.LoginRequest;


import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;

import org.json.JSONObject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private String authToken;

    @BeforeEach
    public void setUp() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setLogin("testUser");
        loginRequest.setPassword("testPassword");
        System.out.println("cm");
        String response = mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\":\"testUser\",\"password\":\"testPassword\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        authToken = "Bearer " + new JSONObject(response).getString("auth-token");
        mockMvc.perform(multipart("/cloud/file")
                        .file("file", "Test file content".getBytes())
                        .header("auth-token", authToken)
                        .param("filename", "testFile.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success upload"));
        mockMvc.perform(multipart("/cloud/file")
                        .file("file", "Test file content!".getBytes())
                        .header("auth-token", authToken)
                        .param("filename", "testFile2.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success upload"));



    }


    @Test
    public void testUploadFile() throws Exception {
        mockMvc.perform(multipart("/cloud/file")
                        .file("file", "Test file content".getBytes())
                        .header("auth-token", authToken)
                        .param("filename", "testFile.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success upload"));
    }

    @Test
    public void testListFiles() throws Exception {
        mockMvc.perform(get("/cloud/list")
                        .header("auth-token", authToken)
                        .param("limit", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/cloud/logout")
                        .header("auth-token", authToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDownloadFile() throws Exception {
        mockMvc.perform(get("/cloud/file")
                        .header("auth-token", authToken)
                        .param("filename", "testFile.txt"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteFile() throws Exception {
        mockMvc.perform(delete("/cloud/file")
                        .header("auth-token", authToken)
                        .param("filename", "testFile.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success deleted"));
    }

    @Test
    public void testRenameFile() throws Exception {
        mockMvc.perform(put("/cloud/file")
                        .header("auth-token", authToken)
                        .param("filename", "testFile2.txt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"newFileName.txt\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success upload"));
    }
}
