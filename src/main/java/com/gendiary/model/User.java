package com.gendiary.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gendiary.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid",unique = true)
    private String uuid;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "gender")
    private String gender;

    @Column(name = "user_role")
    private UserRole role;

    @Column(name = "birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthDate;

    @Column(name = "join_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date joinDate;

    @Column(name = "password")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Post> sharedPosts;

    @JsonIgnore
    @ManyToMany(mappedBy = "likeList")
    private List<Comment> likedComments = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "likeList")
    private List<Post> likedPosts = new ArrayList<>();
}
