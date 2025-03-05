package org.example.securetracks.controller;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.DeliveryDto;
import org.example.securetracks.service.implement.DeliveryService;
import org.example.securetracks.service.implement.ExcelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;
    private final ExcelService excelService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DeliveryDto dto) {
        try {
            DeliveryDto createdDelivery = deliveryService.create(dto);
            return ResponseEntity.ok(Map.of(
                    "message", "Tạo delivery thành công",
                    "data", createdDelivery
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Lỗi tạo delivery: " + e.getMessage()
            ));
        }
    }
//    @PostMapping("/import-excel")
//    public ResponseEntity<?> importDelivery(@RequestParam("file") MultipartFile file) {
//        try {
//            List<DeliveryDto> deliveries = excelService.importDeliveries(file);
//            List<DeliveryDto> savedDeliveries = deliveryService.saveAll(deliveries);
//            return ResponseEntity.ok(Map.of("message", "Import thành công!", "data", savedDeliveries));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi import: " + e.getMessage()));
//        }
//    }



}
