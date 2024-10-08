package com.securenotes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.securenotes.model.Notes;
import com.securenotes.model.User;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateNoteRequest {
    private String title;
    private String description;
    private int userId;
    private String password;

    public Notes to() {
        return Notes.builder()
                .title(this.title)
                .description(this.description)
                .password(this.password)
//                .myUser(
//                        User.builder()
//                                .userId(this.userId)
//                                .build()
//                )
                .build();
    }
}
