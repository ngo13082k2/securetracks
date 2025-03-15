package org.example.securetracks.controller;

import org.example.securetracks.dto.MasterDataDeliveryDto;
import org.example.securetracks.service.IMasterDataDeliveryservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
