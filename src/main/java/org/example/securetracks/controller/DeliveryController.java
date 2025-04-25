package org.example.securetracks.controller;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.DeliveryDto;
import org.example.securetracks.request.CreateDeliveryRequest;
import org.example.securetracks.service.IDeliveryService;
import org.example.securetracks.service.implement.ExcelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {
    private final IDeliveryService deliveryService;
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
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            response.put("message", "File không hợp lệ! Chỉ chấp nhận định dạng .xlsx.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            excelService.importFromExcelDelivery(file);
            response.put("message", "Tải lên và nhập dữ liệu thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Lỗi xử lý file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<DeliveryDto>> getAllDeliveries() {
        List<DeliveryDto> deliveries = deliveryService.getAllDeliveries();
        if (deliveries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(deliveries);
    }
    @PostMapping("/createDelivery")
    public ResponseEntity<String> createDelivery(@RequestBody CreateDeliveryRequest request) {
        String response = deliveryService.createDelivery(request);
        return ResponseEntity.ok(response);
    }



}
