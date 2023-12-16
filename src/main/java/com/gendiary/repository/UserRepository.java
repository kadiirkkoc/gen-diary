package com.gendiary.repository;

import com.gendiary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE uuid = :uuid",nativeQuery = true)
    Optional<User> findByUuid(String uuid);

    @Query(value = "SELECT * FROM users WHERE email = :email",nativeQuery = true)
    User findByEmail(String email);
}
