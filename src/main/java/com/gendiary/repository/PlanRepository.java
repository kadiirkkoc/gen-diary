package com.gendiary.repository;

import com.gendiary.model.Plan;
import com.gendiary.model.PlanFeatureLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}
