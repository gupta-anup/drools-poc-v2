package com.poc.droolspocv2.controller;

import com.poc.droolspocv2.dto.OrderRequest;
import com.poc.droolspocv2.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest request) {
        String response = orderService.placeOrder(request);
        return ResponseEntity.ok(response);
    }
}
