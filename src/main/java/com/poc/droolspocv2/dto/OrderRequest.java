package com.poc.droolspocv2.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderRequest {
    private String accountId;
    private String name;
    private Integer age;
    private LocalDate dob;
    private String product;
    private Integer quantity;
}
