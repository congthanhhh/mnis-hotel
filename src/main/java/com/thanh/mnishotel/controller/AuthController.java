package com.thanh.mnishotel.controller;

import com.thanh.mnishotel.exception.UserAlreadyExistsException;
import com.thanh.mnishotel.model.User;
import com.thanh.mnishotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(User user) {
        try{
            userService.registerUser(user);
            return ResponseEntity.ok("Registration successful!");
        }catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
