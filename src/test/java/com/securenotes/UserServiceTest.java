package com.securenotes;

import com.securenotes.dto.CreateUserRequest;
import com.securenotes.dto.UserResponse;
import com.securenotes.model.User;
import com.securenotes.repository.UserRepository;
import com.securenotes.service.UserService;
import com.securenotes.utils.EmailUtil;
import com.securenotes.utils.OtpUtil;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)//how you want to execute your test cases
public class UserServiceTest {

    @InjectMocks//similar to @Autowired - creates an actual object
    UserService userService;

    @Mock // creates an dummy object
    CreateUserRequest createUserRequest;


    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpUtil otpUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailUtil emailUtil;

    @Mock
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() throws Exception {
        // Arrange
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("existing@example.com");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(new User());

        // Act
        UserResponse response = userService.create(request);

        // Assert
        assertEquals("Email Already Exists!", response.getMessage());
    }

    @Test
    public void CreateTest() throws Exception {

        userService.create(createUserRequest);
    }
}
