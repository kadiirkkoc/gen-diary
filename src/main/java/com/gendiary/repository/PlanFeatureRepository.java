package com.gendiary.repository;

import com.gendiary.model.PlanFeatureLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanFeatureRepository extends JpaRepository<Long, PlanFeatureLimit> {
}
