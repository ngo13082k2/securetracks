package org.example.securetracks.controller;

import org.example.securetracks.dto.OutBoundSummaryDTO;
import org.example.securetracks.dto.OutboundDetailDTO;
import org.example.securetracks.service.IOutBoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/outbounds")
public class OutboundController {

    @Autowired
    private IOutBoundService outboundService;

    // API lấy danh sách Outbound theo ngày hoặc tất cả nếu không có ngày
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOutboundsByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate saleDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Map<String, Object> response = outboundService.getOutboundsByDate(saleDate, page, size);
        return ResponseEntity.ok(response);
    }

    // API lấy chi tiết Outbound theo ID
    @GetMapping("/{outboundId}")
    public ResponseEntity<OutboundDetailDTO> getOutboundById(@PathVariable Long outboundId) {
        OutboundDetailDTO outboundDetail = outboundService.getOutboundById(outboundId);
        return ResponseEntity.ok(outboundDetail);
    }
    @GetMapping("/summaryItem")

    public ResponseEntity<Map<String, Object>> getItemSummary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        Map<String, Object> result = outboundService.getAllUniqueItemNamesWithTotal(start, end, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getOutboundDetails(
            @RequestParam Long itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Map<String, Object> data = outboundService.getOutboundsByItem(itemId, page, size);
        return ResponseEntity.ok(data);
    }

}
