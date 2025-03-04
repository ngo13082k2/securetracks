package org.example.securetracks.service.implement;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.securetracks.model.MasterData;
import org.example.securetracks.repository.MasterDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                    int item = Integer.parseInt(itemStr);
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
}