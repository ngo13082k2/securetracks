package org.example.securetracks.controller;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.InboundDTO;
import org.example.securetracks.service.IInboudService;
import org.example.securetracks.service.implement.InboundService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inbounds")
@RequiredArgsConstructor
public class InboundController {

    private final IInboudService inboundService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getInboundsByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate importDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Map<String, Object> response = inboundService.getInboundsByDate(importDate, page, size);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<InboundDTO> getInboundById(@PathVariable Long id) {
        InboundDTO inboundDTO = inboundService.getInboundById(id);
        return ResponseEntity.ok(inboundDTO);
    }
    @GetMapping("status/active")
    public ResponseEntity<Map<String, Object>> getActiveInbounds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int pageSize) {
        return ResponseEntity.ok(inboundService.getActiveInbounds(page, pageSize));
    }
    @GetMapping("/uniqueItems")
    public ResponseEntity<Map<String, Object>> getInboundSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = inboundService.getAllUniqueItemNamesWithTotal(startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/summaryStatusActive")
    public ResponseEntity<Map<String, Object>> getInboundSummaryStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = inboundService.getAllUniqueItemNamesWithTotalStatus(startDate, endDate, page, size);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/byItem")
    public ResponseEntity<Map<String, Object>> getInboundByItem(
            @RequestParam Long item,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = inboundService.getAllActiveInboundByItem(item, page, size);
        return ResponseEntity.ok(response);
    }
}
