package com.gendiary.controller;

import com.gendiary.beans.AuthenticationResponse;
import com.gendiary.dtos.AuthenticationRequest;
import com.gendiary.dtos.RegisterRequest;
import com.gendiary.dtos.UserDto;
import com.gendiary.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> registration(@RequestBody UserDto request){
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return new ResponseEntity<>(userService.authenticate(request),HttpStatus.OK);
    }


}
