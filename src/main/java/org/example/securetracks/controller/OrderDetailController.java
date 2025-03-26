package org.example.securetracks.controller;

import org.example.securetracks.dto.OrderDetailDTO;
import org.example.securetracks.dto.OrderRequestDTO;
import org.example.securetracks.model.OrderDetail;
import org.example.securetracks.service.IOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderDetailController {
    @Autowired
    private IOrderDetailService orderDetailService;

    @PostMapping
    public ResponseEntity<OrderDetail> createOrder(@RequestBody OrderRequestDTO request) {
        return ResponseEntity.ok(orderDetailService.createOrder(request));
    }
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDetailDTO>> getOrdersByUser() {
        return ResponseEntity.ok(orderDetailService.getOrdersByUser());
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDTO> getOrderDetailById(@PathVariable Long id) {
        OrderDetailDTO dto = orderDetailService.getOrderDetailById(id);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/search")
    public ResponseEntity<List<OrderDetailDTO>> searchOrders(
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<OrderDetailDTO> orders = orderDetailService.searchOrders(phoneNumber, startDate, endDate);
        return ResponseEntity.ok(orders);
    }


}
