package com.gendiary.service;

import com.gendiary.dtos.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> getAllUser();
    UserDto getUserById(Long Id);
    String createUser(UserDto userDto);
    String updateUser(Long id,UserDto userDto);
    String deleteUser(Long id);




}
