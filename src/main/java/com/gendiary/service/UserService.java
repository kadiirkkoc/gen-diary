package com.gendiary.service;

import com.gendiary.beans.AuthenticationResponse;
import com.gendiary.dtos.AuthenticationRequest;
import com.gendiary.dtos.UserDto;
import com.gendiary.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User getDbUserById(Long userId);
    List<UserDto> getAllUser();
    UserDto getUserById(Long Id);
    AuthenticationResponse createUser(UserDto userDto);
    String updateUser(Long id,UserDto userDto);
    String deleteUser(Long id);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void followUser(Long Id);
    void unfollowUser(Long Id);
}
