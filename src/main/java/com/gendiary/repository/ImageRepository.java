package com.gendiary.repository;

import com.gendiary.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Long, Image> {
}
