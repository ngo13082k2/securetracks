package org.example.securetracks.service.implement;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.securetracks.dto.DeliveryDto;
import org.example.securetracks.dto.MasterDataDto;
import org.example.securetracks.model.Delivery;
import org.example.securetracks.model.DeliveryDetail;
import org.example.securetracks.model.MasterData;
import org.example.securetracks.model.MasterDataDelivery;
import org.example.securetracks.model.enums.CalculationUnit;
import org.example.securetracks.repository.DeliveryDetailRepository;
import org.example.securetracks.repository.DeliveryRepository;
import org.example.securetracks.repository.MasterDataDeliveryRepository;
import org.example.securetracks.repository.MasterDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExcelService {

    @Autowired
    private MasterDataRepository masterDataRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private MasterDataDeliveryRepository masterDataDeliveryRepository;
    @Autowired
    private DeliveryDetailRepository deliveryDetailRepository;

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
    public void importFromExcelDelivery(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) {
                throw new RuntimeException("File Excel không có dữ liệu!");
            }
            rowIterator.next(); // Bỏ qua dòng tiêu đề

            List<MasterDataDelivery> masterDataDeliveries = new ArrayList<>();
            List<DeliveryDetail> deliveryDetails = new ArrayList<>();
            int totalQuantity = 0;
            Delivery delivery = null;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (delivery == null) {
                    delivery = new Delivery();
                    delivery.setCalculationUnit(CalculationUnit.valueOf(getCellValueDelivery(row.getCell(0))));
                    delivery.setDeliveryDate(LocalDate.parse(getCellValueDelivery(row.getCell(1))));
                    delivery = deliveryRepository.save(delivery);
                }

                Long itemId = Long.parseLong(getCellValueDelivery(row.getCell(5)));
                int quantity = Integer.parseInt(getCellValueDelivery(row.getCell(6)));
                LocalDate manufacturingDate = LocalDate.parse(getCellValueDelivery(row.getCell(3)));
                LocalDate expireDate = LocalDate.parse(getCellValueDelivery(row.getCell(4)));
                String batch = getCellValueDelivery(row.getCell(2));

                MasterData masterData = masterDataRepository.findById(itemId)
                        .orElseThrow(() -> new IllegalArgumentException("MasterData không tồn tại: " + itemId));

                MasterDataDelivery masterDataDelivery = MasterDataDelivery.builder()
                        .delivery(delivery)
                        .masterData(masterData)
                        .quantity(quantity)
                        .manufaturingDate(manufacturingDate)
                        .expirationDate(expireDate)
                        .batch(batch)
                        .build();

                masterDataDelivery = masterDataDeliveryRepository.save(masterDataDelivery);

                // Tạo DeliveryDetail dựa trên MasterDataDelivery
                int totalBottles = masterData.getSpec() * masterData.getPer() * quantity;
                DeliveryDetail deliveryDetail = DeliveryDetail.builder()
                        .masterDataDelivery(masterDataDelivery)
                        .totalBottles(totalBottles)
                        .build();

                deliveryDetails.add(deliveryDetail);
                totalQuantity += quantity;
            }

            // Cập nhật tổng số lượng trong Delivery
            if (delivery != null) {
                delivery.setQuantity(totalQuantity);
                deliveryRepository.save(delivery);
                deliveryDetailRepository.saveAll(deliveryDetails); // Lưu DeliveryDetail
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel", e);
        }
    }


    private String getCellValueDelivery(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue().trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "UNKNOWN";
        }
    }

}