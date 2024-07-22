package com.stepan.TaskManager.Tasks;

import java.util.Date;

public record TaskDTO(
        Long id,
        String name,
        String description,
        boolean status,
        Date start,
        Date end
) {
    public static TaskDTO fromEntity(TaskEntity taskEntity) {
        return new TaskDTO(
                taskEntity.getId(),
                taskEntity.getName(),
                taskEntity.getDescription(),
                taskEntity.isStatus(),
                taskEntity.getStartDt(),
                taskEntity.getEndDt()
        );
    }
}
