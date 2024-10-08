package com.securenotes.service;
import com.securenotes.dto.CreateUserRequest;
import com.securenotes.dto.LoginRequest;
import com.securenotes.dto.LoginResponse;
import com.securenotes.dto.UserResponse;
import com.securenotes.model.Token;
import com.securenotes.model.User;
import com.securenotes.repository.NotesRepository;
import com.securenotes.repository.TokenRepository;
import com.securenotes.repository.UserRepository;
import com.securenotes.utils.EmailUtil;
import com.securenotes.utils.EncryptionUtil;
import com.securenotes.utils.JWTUtils;
import com.securenotes.utils.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class UserService {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotesRepository notesRepository;

    @Autowired
    OurUserDetailService ourUserDetailService;

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    ExecutorService executorService;

    public UserResponse create(CreateUserRequest createUserRequest) throws Exception {
        UserResponse userResponse = new UserResponse();
        //otp related stuff
        String otp = otpUtil.generateOtp();

        User user = new User();
        user.setEmail(EncryptionUtil.encrypt(createUserRequest.getEmail()));
        user.setName(EncryptionUtil.encrypt(createUserRequest.getName()));
        user.setRole(createUserRequest.getRole());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setOtp(passwordEncoder.encode(otp));
        user.setOtpGenerationTime(LocalDateTime.now());

//        try{
//            emailUtil.sendOtpEmail(createUserRequest.getEmail(),otp);
//        }catch (MessagingException e){
//            throw new RuntimeException("Unable to send the otp, Please try again!");
//        }

        /*
        Before: Email sending was done synchronously, meaning the process of sending an email blocked the main thread until the email was sent.
        This added significant latency, especially if the email server was slow or experiencing delays.
        After: By using ExecutorService to send the email asynchronously, the main thread can proceed with other operations without waiting for the email to be sent.
        This reduces the overall time taken to complete the user creation process.
         */

        // Send OTP email asynchronously
        executorService.submit(() -> {
            try {
                emailUtil.sendOtpEmail(createUserRequest.getEmail(), otp);
            } catch (MessagingException e) {
                // Log the exception and potentially retry or notify admin
                e.printStackTrace();
            }
        });



//        userResponse.setUser(user);
        userResponse = UserResponse.to(user);
        userResponse.setMessage("Sign up successfully. Otp sent to Registered Email, Verify account and login...");

        User userResult =userRepository.save(user);
        return userResponse;
    }
    // Ensure to shut down the executor service appropriately
    public void shutdown() {
        executorService.shutdown();
    }

    public User getUserById(int id){
        return userRepository.findById(id);
    }


    public LoginResponse login(LoginRequest loginRequest) throws Exception {

        LoginResponse loginResponse = new LoginResponse();
//        try{
//             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
//        }catch (AuthenticationException e){
//            loginResponse.setMessage("Wrong email or password");
//            return loginResponse;
//        }
        String encryptedEmail = EncryptionUtil.encrypt(loginRequest.getEmail());
        System.out.println(encryptedEmail+"this is encrypted email");
        var user = userRepository.findByEmail(encryptedEmail);
        var jwt = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

        if(!user.isActive()){
            loginResponse.setMessage("Email is not verified yet, Please verify and retry!");
            return loginResponse;
        }


        //revoke all tokens for a user
        revokeAllTokensByUser(user);
        //save token to db
        saveUserToken(user, jwt, refreshToken);

        loginResponse.setToken(jwt);
        loginResponse.setMessage("Login Successfully!");
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setRole(user.getRole());

        return  loginResponse;
    }

    private void saveUserToken(User user, String jwt, String refreshToken) {
        Token token = new Token();
        token.setToken(jwt);
        token.setRefreshToken(refreshToken);
        token.setUser(user);
        token.setLoggedOut(false);
        tokenRepository.save(token);
    }

    private void revokeAllTokensByUser(User user){
        List<Token>validTokensListByUser = tokenRepository.findAllTokenByUser(user.getUserId());
        if(!validTokensListByUser.isEmpty()){
            validTokensListByUser.forEach(t -> {
                t.setLoggedOut(true);
            });
        }
        tokenRepository.saveAll(validTokensListByUser);
    }

    //verification related
    public String verifyAccount(String email, String otp) throws Exception {
        String encryptedEmail = EncryptionUtil.encrypt(email);
        User user = userRepository.findByEmail(encryptedEmail);
        if(user == null){
            return "User not found with this email: "+email;
        }
        //checking otp from saved otp with entered otp

        if (passwordEncoder.matches(otp,user.getOtp())&& Duration.between(user.getOtpGenerationTime(),
                LocalDateTime.now()).getSeconds() < (2 * 60)) {//checking time of otp
            user.setActive(true);
            userRepository.save(user);
            return "OTP verified you can login";
        }
        return "Please regenerate otp and try again";
    }

    public String regenerateOtp(String email) throws Exception {
        String encryptedEmail = EncryptionUtil.encrypt(email);
        User user = userRepository.findByEmail(encryptedEmail);
        if(user == null){
            return "User not found with this email: "+email;
        }

        String otp = otpUtil.generateOtp();
        // Send OTP email asynchronously
        executorService.submit(() -> {
            try {
                emailUtil.sendOtpEmail(email, otp);
            } catch (MessagingException e) {
                // Log the exception and potentially retry or notify admin
                e.printStackTrace();
                throw  new RuntimeException("unable to send otp please try again");
            }
        });
        user.setOtp(passwordEncoder.encode(otp));
        user.setOtpGenerationTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent... please verify account within 2 minutes";
    }


}
