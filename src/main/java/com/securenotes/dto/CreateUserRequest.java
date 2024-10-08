package com.securenotes.dto;

import com.securenotes.model.User;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CreateUserRequest {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String password;
    @Column(nullable = false, unique = true)
    private String email;

    private String role;


//    public User to() {
//        return User.builder()
//                .username(this.username)
//                .email(this.email)
//                .password(this.password)
//                .build();
//    }
}
