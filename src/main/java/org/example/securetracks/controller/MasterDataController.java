package org.example.securetracks.controller;

import org.example.securetracks.dto.MasterDataDto;
import org.example.securetracks.model.MasterData;
import org.example.securetracks.service.IMasterDataService;
import org.example.securetracks.service.implement.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/master-data")
public class MasterDataController {

    @Autowired
    private IMasterDataService masterDataService;

    @Autowired
    private ExcelService excelService;

    // Tạo mới MasterData
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody MasterDataDto dto) {
        MasterDataDto createdData = masterDataService.create(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tạo mới dữ liệu thành công!");
        response.put("data", createdData);
        return ResponseEntity.ok(response);
    }

    // Lấy danh sách tất cả MasterData
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        List<MasterDataDto> dataList = masterDataService.getAll();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy danh sách thành công!");
        response.put("total", dataList.size());
        response.put("data", dataList);
        return ResponseEntity.ok(response);
    }

    // Lấy dữ liệu theo item
    @GetMapping("/{item}")
    public ResponseEntity<Map<String, Object>> getByItem(@PathVariable Long item) {
        MasterDataDto data = masterDataService.getByItem(item);
        Map<String, Object> response = new HashMap<>();
        response.put("message", data != null ? "Tìm thấy dữ liệu!" : "Không tìm thấy dữ liệu!");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    // Cập nhật dữ liệu theo item
    @PutMapping("/{item}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long item, @RequestBody MasterDataDto dto) {
        MasterDataDto updatedData = masterDataService.update(item, dto);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Cập nhật dữ liệu thành công!");
        response.put("data", updatedData);
        return ResponseEntity.ok(response);
    }

    // Xóa dữ liệu theo item
    @DeleteMapping("/{item}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long item) {
        masterDataService.delete(item);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Xóa dữ liệu thành công!");
        response.put("deletedItem", item);
        return ResponseEntity.ok(response);
    }

    // Upload file Excel (1)
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            response.put("message", "File không hợp lệ! Chỉ chấp nhận định dạng .xlsx.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            excelService.importExcel(file);
            response.put("message", "Tải lên và nhập dữ liệu thành công!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Lỗi xử lý file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // Upload file Excel (2) - Trả về dữ liệu đã import
    @PostMapping("/uploads")
    public ResponseEntity<Map<String, Object>> uploadExcel(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<MasterData> dataList = masterDataService.importExcel(file);
            response.put("message", "Tải lên thành công!");
            response.put("totalImported", dataList.size());
            response.put("data", dataList);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("message", "Lỗi trong quá trình nhập dữ liệu: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
