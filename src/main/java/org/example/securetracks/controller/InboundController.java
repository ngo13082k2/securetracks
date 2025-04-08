package org.example.securetracks.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.InboundDTO;
import org.example.securetracks.model.Inbound;
import org.example.securetracks.service.IInboudService;
import org.example.securetracks.service.implement.ExcelService;
import org.example.securetracks.service.implement.InboundService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inbounds")
@RequiredArgsConstructor
public class InboundController {

    private final IInboudService inboundService;
    private final ExcelService excelService;


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
    @GetMapping("/allInventory")
    public ResponseEntity<Map<String, Object>> getAllActiveInbound(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> response = inboundService.getAllActiveInbound(page, size, startDate, endDate);
        return ResponseEntity.ok(response);
    }



    @PutMapping("/toggle-status")
        public ResponseEntity<String> toggleInboundStatusByQrCode(@RequestParam String qrCode) {
        String response = inboundService.toggleInboundStatusByQrCode(qrCode);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/by-qrcode")
    public ResponseEntity<InboundDTO> getInboundByQrCode(@RequestParam String qrCode) {
        InboundDTO inbound = inboundService.getInboundByQrCode(qrCode);
        return ResponseEntity.ok(inbound);
    }
    @GetMapping("/paged")
    public ResponseEntity<Map<String, Object>> getAllInboundsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> response = inboundService.getAllInboundsPaged(page, size, startDate, endDate);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/export")
    public void exportInboundsToExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inbounds.xlsx");

        inboundService.exportInboundsToExcel(startDate, endDate, response.getOutputStream());
    }





}
