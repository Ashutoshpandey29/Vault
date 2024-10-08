package com.securenotes.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.securenotes.model.Tasks;
import com.securenotes.model.User;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTaskRequest {
    private String title;
    private String description;
    private boolean completed;
    private LocalDate dueDate;
    private User user;

    public Tasks to(){
        return Tasks.builder()
                .title(this.title)
                .description(this.description)
                .completed(this.completed)
                .dueDate(this.dueDate)
                .build();
    }
}
