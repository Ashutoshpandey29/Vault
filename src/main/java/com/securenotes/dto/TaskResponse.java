package com.securenotes.dto;

import com.securenotes.model.Tasks;
import lombok.Data;
import org.springframework.scheduling.config.Task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskResponse {
    private String title;
    private String description;
    private boolean completed;
    private LocalDate dueDate;
    private int userId;

     public static TaskResponse to(Tasks task){
        TaskResponse taskDTO =  new TaskResponse();
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setCompleted(task.isCompleted());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setUserId(task.getUser().getUserId()); // Assuming getUserId() returns the user id
        return taskDTO;
    }

    public static List<TaskResponse> toList(List<Tasks> tasksList){
        List<TaskResponse>taskDto = new ArrayList<>();
        // Convert tasks to TaskDTO objects
        for(Tasks task:tasksList){
            taskDto.add(to(task));
        }
        return taskDto;
    }
}
