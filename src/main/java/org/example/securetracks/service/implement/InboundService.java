package org.example.securetracks.service.implement;

import org.example.securetracks.dto.InboundDTO;
import org.example.securetracks.model.Inbound;
import org.example.securetracks.model.User;
import org.example.securetracks.model.enums.InboundStatus;
import org.example.securetracks.repository.InboundRepository;
import org.example.securetracks.service.IInboudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InboundService implements IInboudService {
    @Autowired
    private UserService userService;
    @Autowired
    private InboundRepository inboundRepository;
    @Autowired
    private ExcelService excelService;
    public Map<String, Object> getInboundsByDate(LocalDate importDate, int page, int size) {
        User currentUser = userService.getCurrentUser(); // ✅ Lấy user đang đăng nhập
        Pageable pageable = PageRequest.of(page, size, Sort.by("importDate").descending());

        Page<Inbound> inboundPage;

        if (importDate != null) {
            inboundPage = inboundRepository.findByImportDateAndUser(importDate, currentUser, pageable);
        } else {
            inboundPage = inboundRepository.findByUser(currentUser, pageable);
        }

        // ✅ Lấy danh sách DTO từ Page
        List<InboundDTO> inboundDTOs = inboundPage.getContent().stream().map(inbound ->
                InboundDTO.builder()
                        .id(inbound.getId())
                        .deliveryId(inbound.getDelivery().getDeliveryId())
                        .item(inbound.getItem())
                        .itemName(inbound.getItemName())
                        .supplier(inbound.getSupplier())
                        .qrCode(inbound.getQrCode())
                        .importDate(inbound.getImportDate())
                        .manufacturingDate(inbound.getManufacturingDate())
                        .expirationDate(inbound.getExpirationDate())
                        .batch(inbound.getBatch())
                        .build()
        ).collect(Collectors.toList());

        // ✅ Tạo response trả về
        Map<String, Object> response = new HashMap<>();
        response.put("total", inboundPage.getTotalElements()); // Tổng số lượng của tất cả các page
        response.put("totalElements", inboundPage.getTotalElements()); // Tổng số phần tử (dùng nếu cần)
        response.put("totalPages", inboundPage.getTotalPages()); // Tổng số trang
        response.put("currentPage", page); // Trang hiện tại
        response.put("data", inboundDTOs); // Dữ liệu của trang hiện tại

        return response;
    }


    public InboundDTO getInboundById(Long inboundId) {
        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inbound not found"));

        return InboundDTO.builder()
                .id(inbound.getId())
                .manufacturingDate(inbound.getManufacturingDate())
                .expirationDate(inbound.getExpirationDate())
                .batch(inbound.getBatch())
                .build();
    }
    public Map<String, Object> getActiveInbounds(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("importDate").descending());

        // Lấy danh sách inbound có status = ACTIVE với phân trang
        Page<Inbound> inboundPage = inboundRepository.findByStatus(InboundStatus.ACTIVE, pageable);

        // Chuyển đổi sang DTO
        List<InboundDTO> inboundDTOs = inboundPage.getContent().stream().map(inbound ->
                InboundDTO.builder()
                        .id(inbound.getId())
                        .deliveryId(inbound.getDelivery().getDeliveryId())
                        .item(inbound.getItem())
                        .itemName(inbound.getItemName())
                        .supplier(inbound.getSupplier())
                        .qrCode(inbound.getQrCode())
                        .importDate(inbound.getImportDate())
                        .manufacturingDate(inbound.getManufacturingDate())
                        .expirationDate(inbound.getExpirationDate())
                        .batch(inbound.getBatch())
                        .status(String.valueOf(inbound.getStatus()))
                        .build()
        ).collect(Collectors.toList());

        // Lấy tổng số inbound có status = ACTIVE
        long totalActive = inboundRepository.countByStatus(InboundStatus.ACTIVE);

        // Tạo response trả về
        Map<String, Object> response = new HashMap<>();
        response.put("total", totalActive);
        response.put("data", inboundDTOs);
        response.put("currentPage", page);
        response.put("pageSize", pageSize);
        response.put("totalPages", inboundPage.getTotalPages());

        return response;
    }
    public Map<String, Object> getAllUniqueItemNamesWithTotal(LocalDate startDate, LocalDate endDate, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách itemName với tổng số lượng theo khoảng thời gian
        Page<Object[]> results = inboundRepository.findItemNamesWithTotal(startDate, endDate, pageable);

        // Lấy tổng toàn bộ số lượng của tất cả itemName
        Long grandTotal = inboundRepository.findTotalQuantity(startDate, endDate);
        if (grandTotal == null) {
            grandTotal = 0L;
        }

        // Xử lý danh sách trả về
        List<Map<String, Object>> data = results.getContent().stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("item", obj[0]);
            map.put("itemName", obj[1]);
            map.put("total", ((Number) obj[2]).intValue());
            return map;
        }).collect(Collectors.toList());

        // Trả về kết quả
        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", results.getTotalElements());
        response.put("totalPages", results.getTotalPages());
        response.put("currentPage", page);
        response.put("grandTotal", grandTotal); // ✅ Tổng toàn bộ số lượng
        response.put("data", data);

        return response;
    }



    public Map<String, Object> getAllUniqueItemNamesWithTotalStatus(LocalDate startDate, LocalDate endDate, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách itemName với tổng số lượng cho cả status = ACTIVE và BLOCKED
        List<InboundStatus> statuses = Arrays.asList(InboundStatus.ACTIVE, InboundStatus.BLOCKED); // Tạo danh sách các status cần lọc

        Page<Object[]> results = inboundRepository.findItemNamesWithTotalStatus(startDate, endDate, statuses, pageable);

        // Lấy tổng toàn bộ số lượng cho cả status = ACTIVE và BLOCKED
        Long grandTotal = inboundRepository.findTotalQuantityStatus(startDate, endDate, statuses);
        if (grandTotal == null) {
            grandTotal = 0L;
        }

        // Xử lý danh sách trả về
        List<Map<String, Object>> data = results.getContent().stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("item", obj[0]);
            map.put("itemName", obj[1]);
            map.put("total", ((Number) obj[2]).intValue());
            return map;
        }).collect(Collectors.toList());

        // Trả về kết quả
        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", results.getTotalElements());
        response.put("totalPages", results.getTotalPages());
        response.put("currentPage", page);
        response.put("grandTotal", grandTotal); // ✅ Tổng toàn bộ số lượng
        response.put("data", data);

        return response;
    }

    public Map<String, Object> getAllActiveInbound(int page, int size, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);

        List<InboundStatus> statuses = Arrays.asList(InboundStatus.ACTIVE, InboundStatus.BLOCKED);

        Page<Inbound> results;

        if (startDate != null && endDate != null) {
            results = inboundRepository.findByStatusInAndImportDateBetween(statuses, startDate, endDate, pageable);
        } else {
            results = inboundRepository.findByStatusIn(statuses, pageable);
        }

        List<InboundDTO> data = results.getContent().stream()
                .map(this::mapDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", page);
        response.put("data", data);
        response.put("totalPages", results.getTotalPages());

        return response;
    }


    public InboundDTO getInboundByQrCode(String qrCode) {
        Inbound inbound = inboundRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Inbound not found for QR Code: " + qrCode));
        return mapDTO(inbound);
    }
    @Transactional
    public String toggleInboundStatusByQrCode(String qrCode) {
        Inbound inbound = inboundRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new RuntimeException("Inbound not found for QR Code: " + qrCode));

        inbound.setStatus(inbound.getStatus() == InboundStatus.ACTIVE ? InboundStatus.BLOCKED : InboundStatus.ACTIVE);

        inboundRepository.save(inbound);

        return "Inbound with QR Code: " + qrCode + " is now " + inbound.getStatus();
    }
    public Map<String, Object> getAllInboundsPaged(int page, int size, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Inbound> inboundPage;

        if (startDate != null && endDate != null) {
            inboundPage = inboundRepository.findByImportDateBetween(startDate, endDate, pageable);
        } else {
            inboundPage = inboundRepository.findAll(pageable);
        }

        List<InboundDTO> inboundDTOs = inboundPage.getContent().stream()
                .map(this::mapDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", page);
        response.put("totalPages", inboundPage.getTotalPages());
        response.put("data", inboundDTOs);

        return response;
    }
    public void exportInboundsToExcel(LocalDate startDate, LocalDate endDate, OutputStream outputStream) throws IOException {
        List<Inbound> inbounds;

        if (startDate != null && endDate != null) {
            inbounds = inboundRepository.findByImportDateBetween(startDate, endDate);
        } else {
            inbounds = inboundRepository.findAll();
        }

        excelService.exportInboundsToExcel(inbounds, outputStream);
    }


    private InboundDTO mapDTO(Inbound inbound) {
        return InboundDTO.builder()
                .id(inbound.getId())
                .item(inbound.getItem())
                .itemName(inbound.getItemName())
                .supplier(inbound.getSupplier())
                .qrCode(inbound.getQrCode())
                .importDate(inbound.getImportDate())
                .manufacturingDate(inbound.getManufacturingDate())
                .expirationDate(inbound.getExpirationDate())
                .batch(inbound.getBatch())
                .status(String.valueOf(inbound.getStatus()))
                .build();
    }
    private Inbound mapEntity(InboundDTO dto) {
        return Inbound.builder()
                .id(dto.getId())
                .item(dto.getItem())
                .itemName(dto.getItemName())
                .supplier(dto.getSupplier())
                .qrCode(dto.getQrCode())
                .importDate(dto.getImportDate())
                .manufacturingDate(dto.getManufacturingDate())
                .expirationDate(dto.getExpirationDate())
                .batch(dto.getBatch())
                .status(InboundStatus.valueOf(dto.getStatus()))
                .build();
    }


}
