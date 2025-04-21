package com.poc.droolspocv2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drools_generic_template_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroolsTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drl_template", columnDefinition = "TEXT")
    private String drlTemplate;

    @Column(name = "validation_type")
    private String validationType;

    @Column(name = "description")
    private String description;
}