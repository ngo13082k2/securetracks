package org.example.securetracks.controller;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.DeliveryDetailDto;
import org.example.securetracks.service.IDeliveryDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-details")
@RequiredArgsConstructor
public class DeliveryDetailController {

    private final IDeliveryDetailService deliveryDetailService;

    @PostMapping("/generate/{deliveryId}")
    public ResponseEntity<String> generateDetails(@PathVariable Long deliveryId) {
        String message = deliveryDetailService.generateDeliveryDetails(deliveryId);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<List<DeliveryDetailDto>> getDetails(@PathVariable Long deliveryId) {
        return ResponseEntity.ok(deliveryDetailService.getDeliveryDetailsByDeliveryId(deliveryId));
    }
}
