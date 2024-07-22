package com.stepan.TaskManager.Users;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
}
