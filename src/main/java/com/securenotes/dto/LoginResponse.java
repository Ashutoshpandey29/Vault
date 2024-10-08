package com.securenotes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private String email;
    private String password;
    private String role;
    private String token;
    private String refreshToken;
    private String message;

}
