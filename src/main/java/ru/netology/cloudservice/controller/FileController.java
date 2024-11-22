package ru.netology.cloudservice.controller;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ru.netology.cloudservice.config.AuthTokenGenerator;
import ru.netology.cloudservice.model.*;
import ru.netology.cloudservice.repository.FileRepository;
import ru.netology.cloudservice.repository.UserRepository;
import ru.netology.cloudservice.service.CustomUserDetailsService;
import ru.netology.cloudservice.service.FileService;

import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/cloud")
public class FileController {
    private static final Logger LOG = LogManager.getLogger(FileController.class);
    private final FileService fileService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Set<String> sessions = new HashSet<>();
    private User currentUser = new User();


    @Autowired
    public FileController(FileService fileService, CustomUserDetailsService customUserDetailsService, FileRepository fileRepository, UserRepository userRepository) {
        sessions.add("admin");
        this.fileService = fileService;
        this.customUserDetailsService = customUserDetailsService;

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        AuthTokenGenerator authGenerator = new AuthTokenGenerator();
        LoginResponse loginResponse = new LoginResponse();
        LOG.info(loginRequest.toString());
        if (customUserDetailsService.containsUsername(loginRequest.getLogin())) {
            User user = customUserDetailsService.loadUser(loginRequest.getLogin());
            currentUser = user;
            if (passwordEncoder.matches(loginRequest.getPassword(),
                    user.getPassword())) {
                String oldToken = user.getAuthToken();
                String newToken = authGenerator.generateToken();
                sessions.add("Bearer " + newToken);
                sessions.remove(oldToken);
                customUserDetailsService.updateAuthToken(oldToken, "Bearer " + newToken);
                loginResponse.setAuthToken(newToken);
                LOG.info(loginResponse.toString());
                return ResponseEntity.ok().body(loginResponse);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Bad credentials", 400));
            }
        } else {
            String newToken = authGenerator.generateToken();
            sessions.add("Bearer " + newToken);
            currentUser = new User(loginRequest.getLogin(), passwordEncoder.encode(loginRequest.getPassword()),
                    "Bearer " + newToken);
            customUserDetailsService.uploadUser(currentUser);
            LOG.info("User registered successfully");
            loginResponse.setAuthToken(newToken);
            return ResponseEntity.ok().body(loginResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") String authToken) {
        LOG.info(String.format("call logout (auth-token: %s)", authToken));
        if (!sessions.contains(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        sessions.remove(authToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("file") File file,
                                        @RequestParam(value = "filename", required = false) String filename) {
        LOG.info(String.format("call uploadFile (auth-token: %s filename: %s)", authToken, filename));
        if (!sessions.contains(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }
        System.out.println(currentUser);
        try {
            fileService.uploadFile(file, filename, currentUser);
            return ResponseEntity.ok(new FileUploadResponse("Success upload"));
        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error uploading file", 400));
        }
    }


    @GetMapping("/list")
    public ResponseEntity<?> listFiles(@RequestHeader("auth-token") String authToken,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit) {
        LOG.info(String.format("call listFiles (auth-token: %s limit: %s currentUserID: %s)",
                authToken, limit, currentUser.getId()));
        if (!sessions.contains(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }
        if (limit > 1 && limit < 100) {
            try {
                List<FileInfo> files = fileService.listFiles(limit, currentUser);
                return ResponseEntity.ok(files);
            } catch (Exception e) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Error getting file list", 500));
            }
        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error input data", 400));
        }

    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename) {
        LOG.info(String.format("call deleteFile (auth-token: %s filename: %s)", authToken, filename));
        if (!sessions.contains(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }

        if (fileService.containsFile(filename, currentUser)) {
            try {
                fileService.deleteFile(filename, currentUser);
                return ResponseEntity.ok(new FileUploadResponse("Success deleted"));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Error delete file", 500));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error input data", 400));
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String oldName,
                                        @RequestBody RenameRequest renameRequest) {
        LOG.info(String.format("call renameFile (auth-token: %s oldName: %s, newName: %s)",
                authToken, oldName, renameRequest.getName()));
        if (!sessions.contains(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }


        if (fileService.containsFile(oldName, currentUser)) {
            try {
                fileService.renameFile(oldName, renameRequest.getName(), currentUser);
                return ResponseEntity.ok(new FileUploadResponse("Success upload"));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Error upload file", 500));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error input data", 400));
        }
    }

    @GetMapping("/file")
    public ResponseEntity<?> downloadFile(@RequestHeader("auth-token") String authToken,
                                          @RequestParam("filename") String filename) {
        LOG.info(String.format("call downloadFile (auth-token: %s filename: %s)", authToken, filename));
        if (!sessions.contains(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }

        System.out.println(currentUser);
        if (fileService.containsFile(filename, currentUser)) {
            try {
                FileEntity fileEntity = fileService.getFile(filename, currentUser);
                File file = new File(fileEntity.getHash(), fileEntity.getFileData());
                return ResponseEntity.ok().body(file);
            } catch (Exception e) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Error upload file", 500));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error input data", 400));
        }
    }
}
