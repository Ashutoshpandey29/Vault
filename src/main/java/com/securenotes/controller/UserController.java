package com.securenotes.controller;

import com.securenotes.dto.CreateUserRequest;
import com.securenotes.dto.LoginRequest;
import com.securenotes.dto.LoginResponse;
import com.securenotes.dto.UserResponse;
import com.securenotes.model.User;
import com.securenotes.repository.UserRepository;
import com.securenotes.service.UserService;
import com.securenotes.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse>signUp(@RequestBody CreateUserRequest createUserRequest) throws Exception {

        User existingUser = userRepository.findByEmail(createUserRequest.getEmail());
        if(existingUser != null){
            UserResponse userResponse = new UserResponse();
            userResponse.setMessage("Email Already Exists!");
            return ResponseEntity.badRequest().body(userResponse);
        }

        return ResponseEntity.ok(userService.create(createUserRequest));
    }

    @PutMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(@RequestParam String email,
                                                @RequestParam String otp) throws Exception {
        return ResponseEntity.ok(userService.verifyAccount(email, otp));
    }

    @PutMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOtp(@RequestParam String email) throws Exception {
        return ResponseEntity.ok(userService.regenerateOtp(email));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(@RequestBody LoginRequest loginRequest) throws Exception {
        String encryptedEmail = EncryptionUtil.encrypt(loginRequest.getEmail());

        LoginResponse loginResponse = new LoginResponse();
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(encryptedEmail,loginRequest.getPassword()));
        }catch (AuthenticationException e){
            loginResponse.setMessage("Wrong email or password");
            return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(loginResponse);
        }
        return ResponseEntity.ok(userService.login(loginRequest));
    }



}
