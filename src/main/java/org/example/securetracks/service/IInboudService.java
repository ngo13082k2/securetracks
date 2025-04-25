package org.example.securetracks.service;

import org.example.securetracks.dto.InboundDTO;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IInboudService {
    InboundDTO getInboundById(Long inboundId);

    Map<String, Object> getInboundsByDate(LocalDate importDate, int page, int size);
    Map<String, Object> getActiveInbounds(int page, int pageSize);
    Map<String, Object> getAllInboundsPaged(int page, int size, LocalDate startDate, LocalDate endDate, String username);
    Map<String, Object> getAllUniqueItemNamesWithTotal(LocalDate startDate, LocalDate endDate, int page, int size, String username);
    Map<String, Object> getAllUniqueItemNamesWithTotalStatus(LocalDate targetDate, String username, int page, int size);
    Map<String, Object> getAllActiveInbound(int page, int size, LocalDate inventoryDate, String username);
    InboundDTO getInboundByQrCode(String qrCode);
    String toggleInboundStatusByQrCode(String qrCode);
    void exportInboundsToExcel(LocalDate inventoryDate, String username, OutputStream outputStream) throws IOException;
    void exportExcel(LocalDate startDate, LocalDate endDate, String username, OutputStream outputStream) throws IOException;
    Map<String, Object> getAllUniqueItemNamesWithTotalByUser(LocalDate startDate, LocalDate endDate, int page, int size);
    Map<String, Object> getAllUniqueItemNamesWithTotalStatusByUser(LocalDate targetDate, int page, int size);
    void exportInboundsToExcelByUser(LocalDate inventoryDate, OutputStream outputStream) throws IOException;
    void exportExcelByUser(LocalDate startDate, LocalDate endDate, OutputStream outputStream) throws IOException;
    Map<String, Object> getAllInboundsPagedByUser(int page, int size, LocalDate startDate, LocalDate endDate);
    Map<String, Object> getAllActiveInboundByUser(int page, int size, LocalDate inventoryDate);
}