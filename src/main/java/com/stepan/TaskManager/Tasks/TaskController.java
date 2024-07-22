package com.stepan.TaskManager.Tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.MissingResourceException;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskService taskService;

    @Value("${spring.jackson.date-format}")
    private String pattern;

    private Pair<Date, Date> parseDate(String strStart, String strEnd)
            throws IllegalArgumentException, ParseException {
        Date start, end;
        start = (new SimpleDateFormat(pattern)).parse(strStart);
        end = (new SimpleDateFormat(pattern)).parse(strEnd);
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException();
        }
        return Pair.of(start, end);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTask(Principal principal, @RequestBody AddRequest addRequest) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anonymous or null principal");
        }
        Date start, end;
        try {
            Pair<Date, Date> tmp = parseDate(addRequest.getStart(), addRequest.getEnd());
            start = tmp.getFirst();
            end = tmp.getSecond();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong date");
        }

        TaskEntity taskEntity;
        try {
            taskEntity = taskService.add(
                    principal, addRequest.getName(), addRequest.getDescription(), addRequest.isStatus(), start, end
            );
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
        }
        return ResponseEntity.ok().body(TaskDTO.fromEntity(taskEntity));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateTask(Principal principal, @RequestBody UpdateRequest updateRequest) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anonymous or null principal");
        }
        Date start, end;
        try {
            Pair<Date, Date> tmp = parseDate(updateRequest.getStart(), updateRequest.getEnd());
            start = tmp.getFirst();
            end = tmp.getSecond();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong date");
        }
        TaskEntity taskEntity;
        try {
            taskEntity = taskService.update(
                    principal, updateRequest.getId(), updateRequest.getName(),
                    updateRequest.getDescription(), updateRequest.isStatus(), start, end
            );
        } catch (MissingResourceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong task id");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to access this resource");
        }
        return ResponseEntity.ok().body(TaskDTO.fromEntity(taskEntity));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTask(Principal principal, @RequestBody DeleteRequest deleteRequest) {
        try {
            taskService.delete(principal, deleteRequest.getId());
        } catch (MissingResourceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong task id");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to access this resource");
        }
        return ResponseEntity.ok().body(String.format("Task %d deleted", deleteRequest.getId()));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTasks(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anonymous or null principal");
        }
        return ResponseEntity.ok().body(taskService.getAll(principal));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getTasks(Principal principal, @RequestBody GetRequest getRequest) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Anonymous or null principal");
        }
        Date start, end;
        try {
            Pair<Date, Date> tmp = parseDate(getRequest.getStart(), getRequest.getEnd());
            start = tmp.getFirst();
            end = tmp.getSecond();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong date");
        }

        return ResponseEntity.ok().body(taskService.getFiltered(principal, getRequest.getName(),
                getRequest.getDescription(), getRequest.isStatus(), start, end));
    }
}
