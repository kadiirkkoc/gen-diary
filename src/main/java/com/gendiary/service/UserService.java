package com.gendiary.service;

import com.gendiary.beans.AuthenticationResponse;
import com.gendiary.dtos.AuthenticationRequest;
import com.gendiary.dtos.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> getAllUser();
    UserDto getUserById(Long Id);
    AuthenticationResponse createUser(UserDto userDto);
    String updateUser(Long id,UserDto userDto);
    String deleteUser(Long id);

    AuthenticationResponse authenticate(AuthenticationRequest request);




}
