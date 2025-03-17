package org.example.securetracks.controller;

import org.example.securetracks.dto.MasterDataDeliveryDto;
import org.example.securetracks.service.IMasterDataDeliveryservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/masterDataDelivery")
public class MasterDataDeliveryController {

    @Autowired
    private IMasterDataDeliveryservice masterDataDeliveryService;

    @GetMapping("/byDelivery")
    public ResponseEntity<List<MasterDataDeliveryDto>> getAllByDeliveryId(@RequestParam Long deliveryId) {
        List<MasterDataDeliveryDto> masterDataDeliveries = masterDataDeliveryService.getAllByDeliveryId(deliveryId);
        if (masterDataDeliveries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(masterDataDeliveries);
    }
    @GetMapping("/delivery/{deliveryId}/items")
    public ResponseEntity<List<Map<String, Object>>> getItemsByDelivery(@PathVariable Long deliveryId) {
        List<Map<String, Object>> items = masterDataDeliveryService.getItemsAndBatchByDelivery(deliveryId);

        if (items.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(items);
    }
}
