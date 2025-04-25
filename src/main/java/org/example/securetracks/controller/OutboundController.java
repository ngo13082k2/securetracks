package org.example.securetracks.controller;

import jakarta.servlet.http.HttpServletResponse;

import org.example.securetracks.dto.OutboundDetailDTO;
import org.example.securetracks.service.IOutBoundService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {



        Map<String, Object> result = outboundService.getAllUniqueItemNamesWithTotal(startDate, endDate, page, size, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/summaryByUser")
    public ResponseEntity<Map<String, Object>> getItemSummaryByUser(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        Map<String, Object> result = outboundService.getAllUniqueItemNamesWithTotalByUser(start, end, page, size);
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
    @GetMapping("/paged")
    public ResponseEntity<Map<String, Object>> getAllOutboundsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username) {

        return ResponseEntity.ok(outboundService.getAllOutboundsPaged(page, size, startDate, endDate, username));
    }

    @GetMapping("/DetailPagedByUser")
    public ResponseEntity<Map<String, Object>> getAllOutboundsPagedByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(outboundService.getAllOutboundsPagedByUser(page, size, startDate, endDate));
    }

    @GetMapping("/export")
    public void exportOutboundsToExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=outbounds.xlsx");

        outboundService.exportOutboundsToExcel(startDate, endDate, username, response.getOutputStream());
    }

    @GetMapping("/exportByUser")
    public void exportOutboundsToExcelByUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=outbounds.xlsx");

        outboundService.exportOutboundsToExcelByUser(startDate, endDate, response.getOutputStream());
    }



}
