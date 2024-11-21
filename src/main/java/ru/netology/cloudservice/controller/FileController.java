package ru.netology.cloudservice.controller;


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
    private final FileService fileService;
    private final CustomUserDetailsService customUserDetailsService;
    private Set<String> sessions = new HashSet<>();
    private final FileRepository fileRepository;
    private  final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();




    @Autowired
    public FileController(FileService fileService, CustomUserDetailsService customUserDetailsService, FileRepository fileRepository, UserRepository userRepository) {
        sessions.add("admin");
        this.fileService = fileService;
        this.customUserDetailsService = customUserDetailsService;
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        System.out.println("mmmmn");
        AuthTokenGenerator authGenerator = new AuthTokenGenerator();
        LoginResponse loginResponse = new LoginResponse();
        if(customUserDetailsService.containsUsername(loginRequest.getLogin())) {
            User user = customUserDetailsService.loadUser(loginRequest.getLogin());
            System.out.println(user.getPassword() + " " + passwordEncoder.encode(loginRequest.getPassword()));
            if(passwordEncoder.matches(loginRequest.getPassword(),
                    user.getPassword())){
                String oldToken = user.getAuthToken();
                String newToken = authGenerator.generateToken();
                sessions.add("Bearer " + newToken);
                sessions.remove(oldToken);
                customUserDetailsService.updateAuthToken(oldToken, "Bearer " + newToken);

                loginResponse.setAuthToken(newToken);
                return ResponseEntity.ok().body(loginResponse);
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Bad credentials", 400));
            }
        }else{
            String newToken = authGenerator.generateToken();
            sessions.add("Bearer " + newToken);
            customUserDetailsService.uploadUser(loginRequest.getLogin(), loginRequest.getPassword(),
                    "Bearer " + newToken);
            System.out.println( "User registered successfully");
            loginResponse.setAuthToken(newToken);
            return ResponseEntity.ok().body(loginResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") String authToken) {

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
        System.out.println(file.getFile() + " " + file.getHash());
        System.out.println(sessions);
        System.out.println(authToken);
        if(!sessions.contains(authToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }
        User userId = customUserDetailsService.getUserByAuthToken(authToken);
        System.out.println(userId);
        try {
            fileService.uploadFile(file, filename, userId);

            return ResponseEntity.ok(new FileUploadResponse("Success upload"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Error uploading file", 400));
        }
    }
//    @GetMapping("/test")
//    public ResponseEntity<?> test(){
//
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        User mockUser = new User();
//        mockUser.setId(2L);
//        mockUser.setUsername("ad");
//        mockUser.setPassword(passwordEncoder.encode("5"));
//        mockUser.setRole("USER");
//        userRepository.save(mockUser);
//        return ResponseEntity.ok(userRepository.findById(2L));
//    }

    @GetMapping("/list")
    public ResponseEntity<?> listFiles(@RequestHeader("auth-token") String authToken,
                                       @RequestParam(value = "limit", defaultValue = "100") int limit) {

        if(!sessions.contains(authToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }

        if (limit > 1 && limit < 100){

            try {

                List<FileInfo> files = fileService.listFiles(limit, customUserDetailsService.getUserByAuthToken(authToken));
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

        if(!sessions.contains(authToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }
        User userId = customUserDetailsService.getUserByAuthToken(authToken);
        if (fileService.containsFile(filename, userId)) {
            try {
                fileService.deleteFile(filename, customUserDetailsService.getUserByAuthToken(authToken));
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
        if(!sessions.contains(authToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }

        User userId = customUserDetailsService.getUserByAuthToken(authToken);
        if (fileService.containsFile(oldName, userId) ) {
            try {
                fileService.renameFile(oldName, renameRequest.getName(), userId);
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


        if(!sessions.contains(authToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Unauthorized", 401));
        }
        User userId = customUserDetailsService.getUserByAuthToken(authToken);
        System.out.println(userId);
        if (fileService.containsFile(filename, userId)){
            try {
                FileEntity fileEntity = fileService.getFile(filename, userId);
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
