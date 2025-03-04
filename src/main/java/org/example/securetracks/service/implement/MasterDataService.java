package org.example.securetracks.service.implement;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.securetracks.dto.MasterDataDto;
import org.example.securetracks.model.MasterData;
import org.example.securetracks.repository.MasterDataRepository;
import org.example.securetracks.service.IMasterDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MasterDataService implements IMasterDataService {

    @Autowired
    private MasterDataRepository masterDataRepository;

    public List<MasterDataDto> getAll() {
        return masterDataRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public MasterDataDto getByItem(Integer item) {
        MasterData data = masterDataRepository.findById(Integer.valueOf(item))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));
        return mapToDto(data);
    }

    public MasterDataDto create(MasterDataDto dto) {
        if (masterDataRepository.existsById(Integer.valueOf(dto.getItem()))) {
            throw new RuntimeException("Item đã tồn tại");
        }
        MasterData entity = mapToEntity(dto);
        return mapToDto(masterDataRepository.save(entity));
    }

    public MasterDataDto update(Integer item, MasterDataDto dto) {
        MasterData existing = masterDataRepository.findById(Integer.valueOf(item))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));

        existing.setName(dto.getName());
        existing.setSpec(dto.getSpec());
        existing.setPer(dto.getPer());
        existing.setCalculationUnit(dto.getCalculationUnit());

        return mapToDto(masterDataRepository.save(existing));
    }

    public void delete(Integer item) {
        masterDataRepository.deleteById(Integer.valueOf(item));
    }
    public List<MasterData> importExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<MasterData> dataList = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            MasterData data = MasterData.builder()
                    .item(Integer.valueOf(row.getCell(0).getStringCellValue()))
                    .name(row.getCell(1).getStringCellValue())
                    .spec(Integer.valueOf(row.getCell(2).getStringCellValue()))
                    .per(Integer.valueOf(row.getCell(3).getStringCellValue()))
                    .calculationUnit(row.getCell(4).getStringCellValue())
                    .build();

            dataList.add(data);
            masterDataRepository.save(data);
        }
        workbook.close();
        return dataList;
    }

    private MasterDataDto mapToDto(MasterData data) {
        return MasterDataDto.builder()
                .item(String.valueOf(data.getItem()))
                .name(data.getName())
                .spec(data.getSpec())
                .per(data.getPer())
                .calculationUnit(data.getCalculationUnit())
                .build();
    }

    private MasterData mapToEntity(MasterDataDto dto) {
        return MasterData.builder()
                .item(Integer.valueOf(dto.getItem()))
                .name(dto.getName())
                .spec(dto.getSpec())
                .per(dto.getPer())
                .calculationUnit(dto.getCalculationUnit())
                .build();
    }
}

