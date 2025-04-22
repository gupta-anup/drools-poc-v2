package com.poc.droolspocv2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    // Order specific fields
    private BigDecimal totalAmount;

    // Customer fields matching AccountValidationData
    private String accountId;
    private Integer age;
    private String name;
    private LocalDate dob;
}
