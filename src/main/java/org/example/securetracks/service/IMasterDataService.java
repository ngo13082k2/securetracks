package org.example.securetracks.service;

import org.example.securetracks.dto.MasterDataDto;
import org.example.securetracks.model.MasterData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IMasterDataService {
    List<MasterDataDto> getAll();
    MasterDataDto getByItem(Long item);
    MasterDataDto create(MasterDataDto dto);
    MasterDataDto update(Long item, MasterDataDto dto);
    void delete(Long item);
    List<MasterData> importExcel(MultipartFile file) throws IOException;
}
