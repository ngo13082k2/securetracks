package org.example.securetracks.service;

import org.example.securetracks.dto.InboundDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IInboudService {
    InboundDTO getInboundById(Long inboundId);

    Map<String, Object> getInboundsByDate(LocalDate importDate, int page, int size);

    Map<String, Object> getActiveInbounds(int page, int pageSize);

    Map<String, Object> getAllUniqueItemNamesWithTotal(LocalDate startDate, LocalDate endDate, int page, int size);
    Map<String, Object> getAllUniqueItemNamesWithTotalStatus(LocalDate startDate, LocalDate endDate, int page, int size);
    Map<String, Object> getAllActiveInboundByItem(Long item, int page, int size);
}