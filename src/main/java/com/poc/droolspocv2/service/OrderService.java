package com.poc.droolspocv2.service;

import com.poc.droolspocv2.dto.OrderRequest;
import com.poc.droolspocv2.dto.ValidationResult;
import com.poc.droolspocv2.model.AccountValidationData;
import com.poc.droolspocv2.model.DroolsTemplate;
import com.poc.droolspocv2.model.Order;
import com.poc.droolspocv2.repository.AccountValidationDataRepository;
import com.poc.droolspocv2.repository.DroolsTemplateRepository;
import com.poc.droolspocv2.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountValidationDataRepository accountRepository;
    private final DroolsTemplateRepository droolsRepository;
    private final DroolsService droolsService;

    private static final String VALIDATION_TYPE = "ORDER_PLACEMENT";

    @Transactional
    public Order placeOrder(OrderRequest orderRequest) {
        // Find customer data from database
        Optional<AccountValidationData> customerDataOpt = accountRepository.findByAccountId(orderRequest.getAccountId());
        if (customerDataOpt.isEmpty()) {
            Order rejectedOrder = Order.builder()
                    .accountId(orderRequest.getAccountId())
                    .orderDate(LocalDateTime.now())
                    .totalAmount(orderRequest.getTotalAmount())
                    .status("REJECTED")
                    .rejectionReason("Customer not found in database")
                    .build();
            return orderRepository.save(rejectedOrder);
        }

        // Find validation template
        Optional<DroolsTemplate> templateOpt = droolsRepository.findByValidationType(VALIDATION_TYPE);
        if (templateOpt.isEmpty()) {
            Order rejectedOrder = Order.builder()
                    .accountId(orderRequest.getAccountId())
                    .orderDate(LocalDateTime.now())
                    .totalAmount(orderRequest.getTotalAmount())
                    .status("REJECTED")
                    .rejectionReason("Validation rules not found")
                    .build();
            return orderRepository.save(rejectedOrder);
        }

        // Validate order request against stored customer data
        AccountValidationData storedCustomer = customerDataOpt.get();
        DroolsTemplate template = templateOpt.get();

        // Pass both request data and stored data to the validation service
        ValidationResult validationResult = droolsService.validateOrder(
                orderRequest,
                storedCustomer,
                template.getDrlTemplate()
        );

        // Create and save order with validation result
        Order order = Order.builder()
                .accountId(orderRequest.getAccountId())
                .orderDate(LocalDateTime.now())
                .totalAmount(orderRequest.getTotalAmount())
                .status(validationResult.isValid() ? "VALIDATED" : "REJECTED")
                .rejectionReason(validationResult.isValid() ? null : validationResult.getMessage())
                .build();

        return orderRepository.save(order);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }
}