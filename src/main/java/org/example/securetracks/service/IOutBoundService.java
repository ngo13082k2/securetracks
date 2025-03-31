package org.example.securetracks.service;

import org.example.securetracks.dto.OutBoundSummaryDTO;
import org.example.securetracks.dto.OutboundDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IOutBoundService {
    OutboundDetailDTO getOutboundById(Long outboundId);
    Map<String, Object> getOutboundsByDate(LocalDate saleDate, int page, int size);
    Map<String, Object> getAllUniqueItemNamesWithTotal(LocalDate startDate, LocalDate endDate, int page, int size);
    Map<String, Object> getOutboundsByItem(Long itemId, int page, int size);
}
