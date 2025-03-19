package org.example.securetracks.controller;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.CustomerMasterDataDTO;
import org.example.securetracks.model.CustomerMasterData;
import org.example.securetracks.service.implement.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerMasterData> createCustomer(@RequestBody CustomerMasterDataDTO request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }
    @GetMapping("/mycustomer")
    public ResponseEntity<List<CustomerMasterData>> getMyCustomers() {
        List<CustomerMasterData> customers = customerService.getCustomersByLoggedInUser();
        return ResponseEntity.ok(customers);
    }
}
