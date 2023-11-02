package com.gendiary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plan_features")
public class PlanFeatureLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "daily_image_limit")
    private Long dailyImageLimit;

    @Column(name = "plan_UUID")
    private String planUUID;

    @Column(name = "feature_id")
    private String featureUUID;
}
