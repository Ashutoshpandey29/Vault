package com.securenotes.service;

import com.securenotes.dto.CreateTaskRequest;
import com.securenotes.exceptions.TaskNotFoundException;
import com.securenotes.model.Tasks;
import com.securenotes.model.User;
import com.securenotes.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private OurUserDetailService ourUserDetailService;

    @Autowired
    private TaskRepository taskRepository;

    public Tasks create(CreateTaskRequest createTaskRequest) {
        Tasks tasks = createTaskRequest.to();

        User loggedInUser = (User)ourUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        tasks.setTitle(createTaskRequest.getTitle());
        tasks.setDescription(createTaskRequest.getDescription());
        tasks.setCompleted(createTaskRequest.isCompleted());
        tasks.setDueDate(createTaskRequest.getDueDate());
        tasks.setUser(loggedInUser);

        return taskRepository.save(tasks);
    }

    public List<Tasks> getAllTasks (){
        User loggedInUser = (User)ourUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        return taskRepository.findTasksByUserId(loggedInUser.getUserId());
    }

    public Tasks getById(int id){
        User loggedInUser = (User)ourUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Tasks task = taskRepository.findById(id).orElse(null);

        if(task != null && task.getUser().getUserId() == loggedInUser.getUserId()){
            return task;
        }else{
            throw new TaskNotFoundException("Task not found or user does not have permission to access this task");
        }
    }

    public Tasks update(int id, CreateTaskRequest createTaskRequest){
        User loggedInUser = (User)ourUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Tasks tasks = getById(id);

        if(tasks != null && tasks.getUser().getUserId() == loggedInUser.getUserId()){
            tasks.setTitle(createTaskRequest.getTitle());
            tasks.setDescription(createTaskRequest.getDescription());
            tasks.setCompleted(createTaskRequest.isCompleted());
            tasks.setDueDate(createTaskRequest.getDueDate());

            return taskRepository.save(tasks);
        }else{
            throw new TaskNotFoundException("Task not found or user does not have permission to access this task");
        }
    }

    public Tasks delete(int id){
        User loggedInUser = (User)ourUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Tasks task = getById(id);
        if(task != null && task.getUser().getUserId() == loggedInUser.getUserId()){
            taskRepository.deleteById(id);
        }
        return task;
    }

    public int deleteAll() {
        User loggedInUser = (User)ourUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        int deletedTasks = taskRepository.deleteAllTasksByUserId(loggedInUser.getUserId());
        return deletedTasks;
    }

    public List<Tasks> getAllByCompletion(boolean completed) {
        return taskRepository.findAllByIsCompleted(completed);
    }
}
