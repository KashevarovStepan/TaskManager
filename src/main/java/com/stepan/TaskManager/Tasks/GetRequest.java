package com.stepan.TaskManager.Tasks;

import lombok.Data;

@Data
public class GetRequest {
    private String name;
    private String description;
    private boolean status;
    private String start;
    private String end;
}
