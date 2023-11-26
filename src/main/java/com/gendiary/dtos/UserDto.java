package com.gendiary.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String firstName;
    private String lastName;
    private String avatarUrl;
    private String username;
    private String email;
    private String gender;
    private Date birthDate;
    private Date joinDate;


}
