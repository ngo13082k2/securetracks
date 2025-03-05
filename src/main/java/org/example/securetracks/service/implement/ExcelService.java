package org.example.securetracks.service.implement;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.securetracks.dto.DeliveryDto;
import org.example.securetracks.dto.MasterDataDto;
import org.example.securetracks.model.MasterData;
import org.example.securetracks.model.enums.CalculationUnit;
import org.example.securetracks.repository.MasterDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExcelService {

    @Autowired
    private MasterDataRepository masterDataRepository;

    public void importExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<MasterData> dataList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bỏ qua dòng tiêu đề
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String itemStr = getCellValue(row.getCell(0));
                if (itemStr.isEmpty()) continue;

                try {
                    long item = Integer.parseInt(itemStr);
                    Optional<MasterData> existingData = masterDataRepository.findByItem(item);
                    MasterData data = existingData.orElse(new MasterData());
                    data.setItem(item);
                    data.setName(getCellValue(row.getCell(1)));
                    data.setSpec(parseInteger(getCellValue(row.getCell(2))));
                    data.setPer(parseInteger(getCellValue(row.getCell(3))));
                    data.setCalculationUnit(getCellValue(row.getCell(4)));

                    dataList.add(data);
                } catch (NumberFormatException e) {
                    System.err.println("Lỗi chuyển đổi số tại dòng: " + (i + 1));
                }
            }

            if (!dataList.isEmpty()) {
                masterDataRepository.saveAll(dataList);
            }
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
//    public List<DeliveryDto> importDeliveries(MultipartFile file) throws IOException {
//        List<DeliveryDto> deliveries = new ArrayList<>();
//
//        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
//            Sheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề (header)
//
//                // Lấy danh sách ID từ file Excel
//                List<Long> masterDataIds = Arrays.stream(row.getCell(6).getStringCellValue().split(","))
//                        .map(Long::parseLong)
//                        .collect(Collectors.toList());
//
//                // Tìm các MasterData theo ID trong database
//                List<MasterDataDto> masterDataDtos = masterDataRepository.findAllById(masterDataIds).stream()
//                        .map(masterData -> new MasterDataDto(masterData.getItem(), masterData.getName(),
//                                masterData.getSpec(), masterData.getPer(),
//                                masterData.getCalculationUnit()))
//                        .collect(Collectors.toList());
//
//                DeliveryDto delivery = DeliveryDto.builder()
////                        .quantity((int) row.getCell(0).getNumericCellValue())
//                        .calculationUnit(CalculationUnit.valueOf(row.getCell(1).getStringCellValue().toUpperCase()))
//                        .deliveryDate(row.getCell(2).getLocalDateTimeCellValue().toLocalDate())
//                        .batch(row.getCell(3).getStringCellValue())
//                        .manufacturingDate(row.getCell(4).getLocalDateTimeCellValue().toLocalDate())
//                        .expireDate(row.getCell(5).getLocalDateTimeCellValue().toLocalDate())
//                        .masterDataItems(masterDataDtos)
//                        .build();
//
//                deliveries.add(delivery);
//            }
//        }
//
//        return deliveries;
//    }
}