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
    Map<String, Object> getAllInboundsPaged(int page, int size, LocalDate startDate, LocalDate endDate);
    Map<String, Object> getAllUniqueItemNamesWithTotal(LocalDate startDate, LocalDate endDate, int page, int size);
    Map<String, Object> getAllUniqueItemNamesWithTotalStatus(LocalDate startDate, LocalDate endDate, int page, int size);
    Map<String, Object> getAllActiveInbound(int page, int size, LocalDate startDate, LocalDate endDate);
    InboundDTO getInboundByQrCode(String qrCode);
    String toggleInboundStatusByQrCode(String qrCode);
    void exportInboundsToExcel(LocalDate startDate, LocalDate endDate, OutputStream outputStream) throws IOException;
}