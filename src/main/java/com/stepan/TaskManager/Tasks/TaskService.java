package com.stepan.TaskManager.Tasks;

import com.stepan.TaskManager.Users.UserEntity;
import com.stepan.TaskManager.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.MissingResourceException;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    private Long getPrincipalId(Principal principal) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException(String.format("There is not user username %s", principal.getName())
                )
        ).getId();
    }

    private void checkAccess(Principal principal, Long taskId)
            throws MissingResourceException, SecurityException, UsernameNotFoundException {
        TaskEntity taskEntity = taskRepository.findTaskById(taskId).orElseThrow(
                () -> new MissingResourceException(String.format("There is not task with id %s", taskId),
                        TaskEntity.class.toString(),
                        taskId.toString())
        );
        if (!taskEntity.getUser().getId().equals(getPrincipalId(principal))) {
            throw new SecurityException("Forbidden");
        }
    }

    public void delete(Principal principal, Long taskId)
            throws UsernameNotFoundException, MissingResourceException, SecurityException {
        checkAccess(principal, taskId);
        taskRepository.deleteById(taskId);
    }

    public TaskEntity add(Principal principal, String name, String description, boolean status, Date start, Date end)
            throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserByUsername(principal.getName()).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("There is not user with username %s", principal.getName())
                )
        );
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName(name);
        taskEntity.setDescription(description);
        taskEntity.setStatus(status);
        taskEntity.setStartDt(start);
        taskEntity.setEndDt(end);
        taskEntity.setUser(userEntity);

        return taskRepository.save(taskEntity);
    }

    public TaskEntity update(Principal principal, Long taskId, String name, String description, boolean status, Date start, Date end)
            throws UsernameNotFoundException, MissingResourceException, SecurityException {
        checkAccess(principal, taskId);
        TaskEntity taskEntity = taskRepository.findTaskById(taskId).orElseThrow(
                () -> new MissingResourceException(String.format("There is not task with id %s", taskId),
                        TaskEntity.class.toString(),
                        taskId.toString())
        );
        taskEntity.setName(name);
        taskEntity.setDescription(description);
        taskEntity.setStatus(status);
        taskEntity.setStartDt(start);
        taskEntity.setEndDt(end);

        return taskRepository.save(taskEntity);
    }

    public Iterable<TaskDTO> getAll(Principal principal) {
        return taskRepository.findByUser(userRepository.findUserByUsername(principal.getName()).orElseThrow(
                        () -> new UsernameNotFoundException(
                                String.format("There is not user with username %s", principal.getName())
                        )
                ))
                .stream()
                .map(TaskDTO::fromEntity)
                .toList();
    }

    public Iterable<TaskDTO> getFiltered(
            Principal principal,
            String name,
            String description,
            boolean status,
            Date start,
            Date end) {
        return taskRepository.findFilteredTasks(userRepository.findUserByUsername(principal.getName()).orElseThrow(
                                () -> new UsernameNotFoundException(
                                        String.format("There is not user with username %s", principal.getName())
                                )
                        ),
                        name, description, status, start, end)
                .stream()
                .map(TaskDTO::fromEntity)
                .toList();
    }
}
