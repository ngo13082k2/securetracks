package org.example.securetracks.service.implement;

import org.example.securetracks.dto.OutBoundSummaryDTO;
import org.example.securetracks.dto.OutboundDTO;
import org.example.securetracks.dto.OutboundDetailDTO;
import org.example.securetracks.model.OutBound;
import org.example.securetracks.model.User;
import org.example.securetracks.repository.OutBoundRepository;
import org.example.securetracks.service.IOutBoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OutBoundService implements IOutBoundService {

    @Autowired
    private OutBoundRepository outboundRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ExcelService excelService;
    // Lấy danh sách Outbound theo ngày hoặc tất cả nếu không có ngày
    public Map<String, Object> getOutboundsByDate(LocalDate saleDate, int page, int size) {
        // ✅ Lấy User đang đăng nhập
        User currentUser = userService.getCurrentUser();
        String currentDealer = currentUser.getUsername(); // Dealer chính là username của User

        Pageable pageable = PageRequest.of(page, size, Sort.by("saleDate").descending());
        Page<OutBound> outboundPage;

        // ✅ Kiểm tra có saleDate hay không
        if (saleDate != null) {
            outboundPage = outboundRepository.findBySaleDateAndDealer(saleDate, currentDealer, pageable);
        } else {
            outboundPage = outboundRepository.findByDealer(currentDealer, pageable);
        }

        // ✅ Lấy danh sách DTO từ Page
        List<OutboundDTO> outboundDTOs = outboundPage.getContent().stream().map(outbound ->
                OutboundDTO.builder()
                        .id(outbound.getId())
                        .item(outbound.getItem())
                        .itemName(outbound.getItemName())
                        .customerName(outbound.getCustomerName())
                        .orderId(outbound.getOrderId())
                        .qrCode(outbound.getQrcode())
                        .saleDate(outbound.getSaleDate())
                        .build()
        ).collect(Collectors.toList());

        // ✅ Tạo response trả về
        Map<String, Object> response = new HashMap<>();
        response.put("total", outboundPage.getTotalElements()); // Tổng số lượng của tất cả các trang
        response.put("totalElements", outboundPage.getTotalElements()); // Tổng số phần tử (dùng nếu cần)
        response.put("totalPages", outboundPage.getTotalPages()); // Tổng số trang
        response.put("currentPage", page); // Trang hiện tại
        response.put("data", outboundDTOs); // Dữ liệu của trang hiện tại

        return response;
    }

    public Map<String, Object> getAllUniqueItemNamesWithTotal(LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Lấy tổng số lượng của từng itemName có phân trang
        Page<Object[]> results = outboundRepository.findItemNamesWithTotal(startDate, endDate, pageable);

        // Lấy tổng số lượng của tất cả itemName (không phân trang)
        Long totalQuantity = outboundRepository.findTotalQuantity(startDate, endDate);
        if (totalQuantity == null) {
            totalQuantity = 0L;
        }

        List<Map<String, Object>> data = results.getContent().stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("item", obj[0]);  // obj[0]: item (Long hoặc Integer)
            map.put("itemName", obj[1]);  // obj[1]: itemName (String)
            map.put("total", ((Number) obj[2]).intValue());  // obj[2]: tổng số lượng của itemName (có phân trang)
            return map;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", results.getTotalElements());
        response.put("totalPages", results.getTotalPages());
        response.put("currentPage", page);
        response.put("grandTotal", totalQuantity); // ✅ Tổng tất cả số lượng của mọi itemName
        response.put("data", data);

        return response;
    }

    public Map<String, Object> getOutboundsByItem(Long itemId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OutBound> resultPage = outboundRepository.findByItem(itemId, pageable);

        List<OutboundDTO> outboundDTOs = resultPage.getContent().stream().map(outbound ->
                OutboundDTO.builder()
                        .id(outbound.getId())
                        .item(outbound.getItem())
                        .itemName(outbound.getItemName())
                        .customerName(outbound.getCustomerName())
                        .orderId(outbound.getOrderId())
                        .qrCode(outbound.getQrcode())
                        .saleDate(outbound.getSaleDate())
                        .manufacturingDate(outbound.getManufacturingDate())
                        .expirationDate(outbound.getExpirationDate())
                        .batch(outbound.getBatch())
                        .build()
        ).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", resultPage.getTotalElements());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("currentPage", page);
        response.put("data", outboundDTOs);

        return response;
    }
    public Map<String, Object> getAllOutboundsPaged(int page, int size, LocalDate startDate, LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OutBound> outboundPage;

        if (startDate != null && endDate != null) {
            outboundPage = outboundRepository.findBySaleDateBetween(startDate, endDate, pageable);
        } else {
            outboundPage = outboundRepository.findAll(pageable);
        }

        List<OutboundDTO> dtos = outboundPage.getContent().stream()
                .map(this::mapDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", page);
        response.put("data", dtos);
        response.put("size", size);
        response.put("totalPages", outboundPage.getTotalPages());
        response.put("totalElements", outboundPage.getTotalElements());
        response.put("isLast", outboundPage.isLast());

        return response;
    }

    public void exportOutboundsToExcel(LocalDate startDate, LocalDate endDate, OutputStream outputStream) throws IOException {
        List<OutBound> outbounds;

        if (startDate != null && endDate != null) {
            outbounds = outboundRepository.findBySaleDateBetween(startDate, endDate);
        } else {
            outbounds = outboundRepository.findAll();
        }

        excelService.exportOutboundsToExcel(outbounds, outputStream);
    }



    private OutboundDTO mapDTO(OutBound outBound) {
        return OutboundDTO.builder()
                .id(outBound.getId())
                .saleDate(outBound.getSaleDate())
                .orderId(outBound.getOrderId())
                .customerName(outBound.getCustomerName())
                .phoneNumber(outBound.getPhoneNumber())
                .qrCode(outBound.getQrcode())
                .item(outBound.getItem())
                .itemName(outBound.getItemName())
                .manufacturingDate(outBound.getManufacturingDate())
                .expirationDate(outBound.getExpirationDate())
                .batch(outBound.getBatch())
                .dealer(outBound.getDealer())
                .build();
    }

    private OutBound mapEntity(OutboundDTO dto) {
        return OutBound.builder()
                .id(dto.getId())
                .saleDate(dto.getSaleDate())
                .orderId(dto.getOrderId())
                .customerName(dto.getCustomerName())
                .phoneNumber(dto.getPhoneNumber())
                .qrcode(dto.getQrCode())
                .item(dto.getItem())
                .itemName(dto.getItemName())
                .manufacturingDate(dto.getManufacturingDate())
                .expirationDate(dto.getExpirationDate())
                .batch(dto.getBatch())
                .dealer(dto.getDealer())
//                .quantity(dto.getQuantity())
                .build();
    }




    // Lấy chi tiết Outbound theo ID
    public OutboundDetailDTO getOutboundById(Long outboundId) {
        OutBound outbound = outboundRepository.findById(outboundId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Outbound not found"));

        return OutboundDetailDTO.builder()
                .id(outbound.getId())
                .masterDataName(outbound.getItemName())
                .expirationDate(outbound.getExpirationDate())
                .batch(outbound.getBatch())
                .dealer(outbound.getDealer())
                .build();
    }
}
