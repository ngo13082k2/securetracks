package org.example.securetracks.controller;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.CustomerMasterDataDTO;
import org.example.securetracks.model.CustomerMasterData;
import org.example.securetracks.service.ICustomerService;
import org.example.securetracks.service.implement.ExcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;
    private final ExcelService excelService;

    @PostMapping
    public ResponseEntity<CustomerMasterData> createCustomer(@RequestBody CustomerMasterDataDTO request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }
    @GetMapping
    public ResponseEntity<List<CustomerMasterDataDTO>> getAllCustomers() {
        List<CustomerMasterDataDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    @GetMapping("/search")
    public ResponseEntity<CustomerMasterDataDTO> getCustomerByPhoneNumber(@RequestParam String phoneNumber) {
        CustomerMasterDataDTO customer = customerService.getCustomerByPhoneNumber(phoneNumber);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }
    @PostMapping("/import")
    public ResponseEntity<String> importFromExcel(@RequestParam("file") MultipartFile file) {
        excelService.importCustomersFromExcel(file);
        return ResponseEntity.ok("Import thành công!");
    }
}
