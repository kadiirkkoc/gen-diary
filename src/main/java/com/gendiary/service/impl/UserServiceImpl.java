package com.gendiary.service.impl;

import com.gendiary.dtos.UserDto;
import com.gendiary.enums.UserRole;
import com.gendiary.loggers.MainLogger;
import com.gendiary.loggers.messages.UserMessage;
import com.gendiary.model.User;
import com.gendiary.repository.UserRepository;
import com.gendiary.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final MainLogger logger = new MainLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAllUser() {
        return userRepository.findAll().stream()
                .map(user -> UserDto.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .avatarUrl(user.getAvatarUrl())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .gender(user.getGender())
                        .birthDate(user.getBirthDate())
                        .joinDate(user.getJoinDate())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(x -> UserDto.builder()
                        .firstName(x.getFirstName())
                        .lastName(x.getLastName())
                        .avatarUrl(x.getAvatarUrl())
                        .username(x.getUsername())
                        .email(x.getEmail())
                        .gender(x.getGender())
                        .birthDate(x.getBirthDate())
                        .joinDate(x.getJoinDate())
                        .build())
                .orElseGet(() -> {
                    logger.log(UserMessage.NOT_FOUND + id, HttpStatus.BAD_REQUEST);
                    return null;
                });
    }

    @Override
    public String createUser(UserDto userDto) {
       User dbUser = User.builder()
               .firstName(userDto.getFirstName())
               .lastName(userDto.getLastName())
               .avatarUrl(userDto.getAvatarUrl())
               .username(userDto.getUsername())
               .email(userDto.getEmail())
               .gender(userDto.getGender())
               .birthDate(userDto.getBirthDate())
               .joinDate(userDto.getJoinDate())
               .password(passwordEncoder.encode(userDto.getPassword()))
               .uuid(UUID.randomUUID().toString())
               .build();
       userRepository.save(dbUser);
       return UserMessage.CREATE+dbUser.getId();
    }

    @Override
    public String updateUser(Long id,UserDto userDto) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()){
            logger.log(UserMessage.NOT_FOUND + id,HttpStatus.BAD_REQUEST);
        }
        user.get().setFirstName(userDto.getFirstName());
        user.get().setLastName(userDto.getLastName());
        user.get().setAvatarUrl(userDto.getAvatarUrl());
        user.get().setUsername(userDto.getUsername());
        user.get().setEmail(userDto.getEmail());
        user.get().setGender(userDto.getGender());
        user.get().setBirthDate(userDto.getBirthDate());
        user.get().setJoinDate(userDto.getJoinDate());
        userRepository.save(user.get());
        return UserMessage.UPDATE + user.get().getId();
    }

    @Override
    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return UserMessage.DELETE + id;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username); // username is email
        if(user == null)
            logger.log("Invalid username or password",HttpStatus.BAD_REQUEST);
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),mapRolesToAuthorities(Collections.singleton(user.getRole())));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<UserRole> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
    }
}
