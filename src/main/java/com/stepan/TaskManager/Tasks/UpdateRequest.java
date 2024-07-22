package com.stepan.TaskManager.Tasks;

import lombok.Data;

@Data
public class UpdateRequest {
    private Long id;
    private String name;
    private String description;
    private boolean status;
    private String start;
    private String end;
}
