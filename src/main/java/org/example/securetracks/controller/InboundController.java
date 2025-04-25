package org.example.securetracks.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.InboundDTO;
import org.example.securetracks.service.IInboudService;
import org.example.securetracks.service.implement.ExcelService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
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
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = inboundService.getAllUniqueItemNamesWithTotal(startDate, endDate, page, size, username);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/stock")
    public ResponseEntity<Map<String, Object>> getStockAsOfDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = inboundService.getAllUniqueItemNamesWithTotalStatus(date, username, page, size);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/stockByUser")
    public ResponseEntity<Map<String, Object>> getStockAsOfDateByUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = inboundService.getAllUniqueItemNamesWithTotalStatusByUser(date, page, size);
        return ResponseEntity.ok(response);
}
    @GetMapping("/allInventory")
    public ResponseEntity<Map<String, Object>> getAllActiveInbound(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inventoryDate,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> result = inboundService.getAllActiveInbound(page, size, inventoryDate, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/allInventoryByUser")
    public ResponseEntity<Map<String, Object>> getAllActiveInboundByUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inventoryDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Gọi service mà không cần truyền userId từ controller
        Map<String, Object> result = inboundService.getAllActiveInboundByUser(page, size, inventoryDate);
        return ResponseEntity.ok(result);
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username) {

        Map<String, Object> response = inboundService.getAllInboundsPaged(page, size, startDate, endDate, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/DetailpagedByUser")
    public ResponseEntity<Map<String, Object>> getAllInboundsPagedByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> response = inboundService.getAllInboundsPagedByUser(page, size, startDate, endDate);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/exportInventory")
    public void exportInventoryToExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inventoryDate,
            @RequestParam(required = false) String username,
            HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inventory.xlsx");

        OutputStream outputStream = response.getOutputStream();
        inboundService.exportInboundsToExcel(inventoryDate, username, outputStream);
        outputStream.flush();
    }

    @GetMapping("/exportInventoryByUser")
    public void exportInventoryToExcelByUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inventoryDate,
            HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=inventory.xlsx");

        OutputStream outputStream = response.getOutputStream();
        inboundService.exportInboundsToExcelByUser(inventoryDate, outputStream);
        outputStream.flush();
    }

    @GetMapping("/exportInbound")
    public void exportInboundsToExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            HttpServletResponse response) {

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "inbounds_" + LocalDate.now() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            inboundService.exportExcel(startDate, endDate, username, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất Excel", e);
        }
    }

    @GetMapping("/exportInboundByUser")
    public void exportInboundsToExcelByUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "inbounds_" + LocalDate.now() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            inboundService.exportExcelByUser(startDate, endDate, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xuất Excel", e);
        }
    }

    @GetMapping("/summary-by-user")
    public ResponseEntity<?> getSummaryByUser(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(inboundService.getAllUniqueItemNamesWithTotalByUser(startDate, endDate, page, size));
    }

}
