// package com.poc.droolspocv2.service;

// import com.poc.droolspocv2.dto.OrderRequest;
// import com.poc.droolspocv2.model.AccountValidationData;
// import com.poc.droolspocv2.model.Order;
// import com.poc.droolspocv2.model.ValidationResult;
// import com.poc.droolspocv2.repository.AccountValidationDataRepository;
// import com.poc.droolspocv2.repository.OrderRepository;
// import lombok.RequiredArgsConstructor;
// import org.kie.api.runtime.KieSession;
// import org.springframework.stereotype.Service;

// import java.time.LocalDate;

// @Service
// @RequiredArgsConstructor
// public class OrderService {

// private final KieSession kieSession;
// private final OrderRepository orderRepository;
// private final AccountValidationDataRepository accountRepo;

// public String placeOrder(OrderRequest request) {
// ValidationResult validationResult = new ValidationResult();

// AccountValidationData input = AccountValidationData.builder()
// .accountId(request.getAccountId())
// .name(request.getName())
// .age(request.getAge())
// .dob(request.getDob())
// .build();

// kieSession.insert(input);
// kieSession.setGlobal("validationResult", validationResult);
// kieSession.insert(validationResult);
// kieSession.fireAllRules();

// if (!validationResult.getMessages().isEmpty()) {
// return "Validation failed: " + String.join(", ",
// validationResult.getMessages());
// }

// Order order = Order.builder()
// .accountId(request.getAccountId())
// .product(request.getProduct())
// .quantity(request.getQuantity())
// .orderDate(LocalDate.now())
// .build();

// orderRepository.save(order);
// return "Order placed successfully";
// }
// }