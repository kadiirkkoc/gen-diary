package com.gendiary.repository;

import com.gendiary.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Long, Tag> {
}
