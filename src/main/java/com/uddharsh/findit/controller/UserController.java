package com.uddharsh.findit.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.uddharsh.findit.dto.CreateUserRequest;
import com.uddharsh.findit.dto.UserResponse;
import com.uddharsh.findit.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return UserResponse.from(userService.createUser(request.name(), request.email()));
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return UserResponse.from(userService.getUser(id));
    }
}