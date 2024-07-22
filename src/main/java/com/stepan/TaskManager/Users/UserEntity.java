package com.stepan.TaskManager.Users;

import com.stepan.TaskManager.Tasks.TaskEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "id", nullable = false, unique = true)
    private Long id;
    private String username;
    private String password;
    private String email;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<TaskEntity> tasks;
}
