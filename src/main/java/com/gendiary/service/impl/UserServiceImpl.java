package com.gendiary.service.impl;

import com.gendiary.beans.AuthenticationResponse;
import com.gendiary.dtos.AuthenticationRequest;
import com.gendiary.dtos.UserDto;
import com.gendiary.enums.UserRole;
import com.gendiary.loggers.MainLogger;
import com.gendiary.loggers.messages.UserMessage;
import com.gendiary.model.User;
import com.gendiary.repository.UserRepository;
import com.gendiary.security.JwtService;
import com.gendiary.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserCredentialsServiceImpl userCredentialsService;
    private final MainLogger logger = new MainLogger(UserServiceImpl.class);


    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, UserCredentialsServiceImpl userCredentialsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userCredentialsService = userCredentialsService;
    }

    @Override
    public User getDbUserById(Long userId) {
        return userRepository.findById(userId).orElseGet(logger.log(UserMessage.NOT_FOUND + userId));
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
                        .country(user.getCountry())
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
                        .country(x.getCountry())
                        .birthDate(x.getBirthDate())
                        .joinDate(x.getJoinDate())
                        .build())
                .orElseGet(() -> {
                    logger.log(UserMessage.NOT_FOUND + id, HttpStatus.BAD_REQUEST);
                    return null;
                });
    }

    @Override
    public AuthenticationResponse createUser(UserDto userDto) {
       var dbUser = User.builder()
               .firstName(userDto.getFirstName())
               .lastName(userDto.getLastName())
               .avatarUrl(userDto.getAvatarUrl())
               .username(userDto.getUsername())
               .email(userDto.getEmail())
               .gender(userDto.getGender())
               .country(userDto.getCountry())
               .birthDate(userDto.getBirthDate())
               .joinDate(userDto.getJoinDate())
               .password(passwordEncoder.encode(userDto.getPassword()))
               .uuid(UUID.randomUUID().toString())
               .role(UserRole.USER)
               .build();
       userRepository.save(dbUser);
       String token = jwtService.generateToken(dbUser);
       return AuthenticationResponse.builder()
               .token(token)
               .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail());

        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
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
        user.get().setCountry(userDto.getCountry());
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

    public final User getAuthenticatedUser() {
        UserDetails output =  userCredentialsService.getActiveUser();
        return getUserByEmail(output.getUsername());
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void followUser(Long id) {
        User authUser = getAuthenticatedUser();
        if (!authUser.getId().equals(id)) {
            Optional<User> userToFollow = userRepository.findById(id);
            authUser.getFollowingUsers().add(userToFollow.get());
            authUser.setFollowingCount(authUser.getFollowingCount() + 1);
            userToFollow.get().getFollowerUsers().add(authUser);
            userToFollow.get().setFollowerCount(userToFollow.get().getFollowerCount() + 1);
            userRepository.save(userToFollow.get());
            userRepository.save(authUser);
        } else {
            logger.log(UserMessage.NOT_FOUND + id,HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void unfollowUser(Long id) {
        User authUser = getAuthenticatedUser();
        if (!authUser.getId().equals(id)) {
            Optional<User> userToUnfollow = userRepository.findById(id);
            authUser.getFollowingUsers().remove(userToUnfollow);
            authUser.setFollowingCount(authUser.getFollowingCount() - 1);
            userToUnfollow.get().getFollowerUsers().remove(authUser);
            userToUnfollow.get().setFollowerCount(userToUnfollow.get().getFollowerCount() - 1);
            userRepository.save(userToUnfollow.get());
            userRepository.save(authUser);
        } else {
            logger.log(UserMessage.NOT_FOUND + id,HttpStatus.BAD_REQUEST);
        }
    }
}
