package com.poc.droolspocv2.repository;

import com.poc.droolspocv2.model.DroolsTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DroolsTemplateRepository extends JpaRepository<DroolsTemplate, Long> {
    Optional<DroolsTemplate> findByValidationType(String validationType);
}