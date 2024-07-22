package com.stepan.TaskManager.Tasks;

import com.stepan.TaskManager.Users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findTaskById(Long id);

    List<TaskEntity> findByUser(UserEntity user);

    @Query(value = "SELECT t FROM TaskEntity t WHERE t.user = ?1 "
            + " AND t.name LIKE %?2% AND t.description LIKE %?3% "
            + " AND ((NOT CAST(?4 AS boolean)) OR t.status) "
            + " AND t.endDt > ?5 AND t.startDt < ?6")
    List<TaskEntity> findFilteredTasks(
            UserEntity user,
            String name,
            String description,
            boolean status,
            Date start,
            Date end
    );
}
