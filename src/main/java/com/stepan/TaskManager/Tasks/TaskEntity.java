package com.stepan.TaskManager.Tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.stepan.TaskManager.Users.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "tasks")
@Data
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "id", nullable = false, unique = true)
    private Long id;
    private String name;
    private String description;
    private boolean status;
    @JsonFormat
    @Column(name = "start_dt")
    private Date startDt;
    @JsonFormat
    @Column(name = "end_dt")
    private Date endDt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
