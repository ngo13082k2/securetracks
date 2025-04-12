package org.example.securetracks.service;

import org.example.securetracks.dto.OutBoundSummaryDTO;
import org.example.securetracks.dto.OutboundDTO;
import org.example.securetracks.dto.OutboundDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IOutBoundService {
    OutboundDetailDTO getOutboundById(Long outboundId);
    Map<String, Object> getOutboundsByDate(LocalDate saleDate, int page, int size);
    Map<String, Object> getAllUniqueItemNamesWithTotal(LocalDate startDate, LocalDate endDate, int page, int size);
    Map<String, Object> getOutboundsByItem(Long itemId, int page, int size);
    Map<String, Object> getAllOutboundsPaged(int page, int size, LocalDate startDate, LocalDate endDate);
    void exportOutboundsToExcel(LocalDate startDate, LocalDate endDate, OutputStream outputStream) throws IOException;
}
