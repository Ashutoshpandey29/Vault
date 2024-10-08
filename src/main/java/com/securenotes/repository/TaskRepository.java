package com.securenotes.repository;

import com.securenotes.model.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, Integer> {

    @Query("select t from Tasks t where t.user.id = :userId")
    List<Tasks> findTasksByUserId(int userId);

    @Transactional
    @Modifying
    @Query("delete from Tasks where user.userId = :userId")
    int deleteAllTasksByUserId(int userId);

    @Query("select t from Tasks t where t.completed = :completed")
    List<Tasks> findAllByIsCompleted(boolean completed);
}
