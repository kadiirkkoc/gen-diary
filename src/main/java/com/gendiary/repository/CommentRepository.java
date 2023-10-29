package com.gendiary.repository;

import com.gendiary.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Long, Comment> {
}
