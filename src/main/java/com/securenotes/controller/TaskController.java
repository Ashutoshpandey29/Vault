package com.securenotes.controller;

import com.securenotes.dto.CreateTaskRequest;
import com.securenotes.dto.TaskResponse;
import com.securenotes.model.Tasks;
import com.securenotes.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController {

    @Autowired
    TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<TaskResponse>createTask(@RequestBody CreateTaskRequest createTaskRequest){
        Tasks tasks = taskService.create(createTaskRequest);
        TaskResponse  taskResponse = TaskResponse.to(tasks);
        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<TaskResponse>getById(@PathVariable ("id")int id){
        Tasks tasks = taskService.getById(id);
        TaskResponse taskResponse = TaskResponse.to(tasks);
        return ResponseEntity.ok(taskResponse);
    }

    @GetMapping("/getAll")
    public List<TaskResponse> getAll(){
        List<Tasks> tasks = taskService.getAllTasks();
        List<TaskResponse> taskResponse = TaskResponse.toList(tasks);
        return taskResponse;
    }

    @GetMapping("/getByCompletion")
    public List<TaskResponse>getTasksByCompletionStatus(@RequestParam(name = "isCompleted") boolean isCompleted){
        List<Tasks> tasks = taskService.getAllByCompletion(isCompleted);
        return TaskResponse.toList(tasks);
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable("id")int id, @RequestBody CreateTaskRequest createTaskRequest){
        Tasks tasks = taskService.getById(id);
        tasks = taskService.update(id,createTaskRequest);
        TaskResponse taskResponse = TaskResponse.to(tasks);//converting to dto
        return ResponseEntity.ok(taskResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<TaskResponse>delete(@PathVariable("id") int id){
        Tasks tasks = taskService.getById(id);
        tasks = taskService.delete(id);
        TaskResponse taskResponse = TaskResponse.to(tasks);
        return ResponseEntity.ok(taskResponse);
    }

    @DeleteMapping("/delete/all")
    public int deleteAll(){
        return taskService.deleteAll();
    }

}
