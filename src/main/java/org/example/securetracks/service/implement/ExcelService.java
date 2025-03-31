package org.example.securetracks.service.implement;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.securetracks.dto.DeliveryDto;
import org.example.securetracks.dto.MasterDataDto;
import org.example.securetracks.model.*;
import org.example.securetracks.model.enums.CalculationUnit;
import org.example.securetracks.model.enums.InboundStatus;
import org.example.securetracks.repository.*;
import org.example.securetracks.response.BottleQrCodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
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
    @Autowired
    private UserService userService;
    @Autowired
    private BottleQrCodeService bottleQrCodeService;
    @Autowired
    private BottleQrCodeRepository bottleQrCodeRepository;
    @Autowired
    private InboundRepository inboundRepository;
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
    @Transactional
    public void importFromExcelDelivery(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            if (!rowIterator.hasNext()) {
                throw new RuntimeException("File Excel không có dữ liệu!");
            }
            rowIterator.next(); // Bỏ qua dòng tiêu đề

            User currentUser = userService.getCurrentUser();
            Map<Long, Delivery> deliveryMap = new HashMap<>();
            List<DeliveryDetail> deliveryDetails = new ArrayList<>();
            List<Inbound> inbounds = new ArrayList<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Long deliveryId = Long.parseLong(getCellValueDelivery(row.getCell(0)));

                // 🔹 Kiểm tra Delivery ID chỉ một lần (dòng đầu tiên của ID đó)
                if (!deliveryMap.containsKey(deliveryId)) {
                    if (deliveryRepository.existsById(deliveryId)) {
                        throw new RuntimeException("Delivery ID: " + deliveryId + " đã tồn tại, không thể nhập dữ liệu!");
                    }

                    // Nếu chưa tồn tại, tạo mới
                    Delivery delivery = new Delivery();
                    delivery.setDeliveryId(deliveryId);
                    delivery.setCalculationUnit(CalculationUnit.valueOf(getCellValueDelivery(row.getCell(1))));
                    delivery.setDeliveryDate(LocalDate.parse(getCellValueDelivery(row.getCell(2))));
                    delivery.setOwner(currentUser);
                    delivery.setQuantity(0); // Khởi tạo quantity = 0
                    delivery = deliveryRepository.save(delivery);

                    deliveryMap.put(deliveryId, delivery);
                }

                // Lấy delivery đã tạo (hoặc lấy từ map)
                Delivery delivery = deliveryMap.get(deliveryId);

                // Tiếp tục lấy dữ liệu từ Excel
                String batch = getCellValueDelivery(row.getCell(3));
                LocalDate manufacturingDate = LocalDate.parse(getCellValueDelivery(row.getCell(4)));
                LocalDate expireDate = LocalDate.parse(getCellValueDelivery(row.getCell(5)));
                Long itemId = Long.parseLong(getCellValueDelivery(row.getCell(6)));
                int quantity = Integer.parseInt(getCellValueDelivery(row.getCell(7)));

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

                // Tạo DeliveryDetail
                int totalBottles = masterData.getSpec() * masterData.getPer() * quantity;
                DeliveryDetail deliveryDetail = DeliveryDetail.builder()
                        .masterDataDelivery(masterDataDelivery)
                        .totalBottles(totalBottles)
                        .build();

                deliveryDetails.add(deliveryDetail);

                // 🔹 Cập nhật totalQuantity vào Delivery ngay khi xử lý xong dòng hiện tại
                delivery.setQuantity(delivery.getQuantity() + quantity);
            }

            // 🔹 Lưu lại tất cả Delivery sau khi cập nhật quantity
            for (Delivery delivery : deliveryMap.values()) {
                deliveryRepository.save(delivery);
            }

            // Lưu DeliveryDetail nếu có
            if (!deliveryDetails.isEmpty()) {
                deliveryDetailRepository.saveAll(deliveryDetails);
            }

            // 🔹 Gọi hàm tự động tạo QR Code sau khi lưu dữ liệu
            bottleQrCodeService.generateQrCodesForAllDeliveries();

            // 🔹 Sau khi đã có QR Code, lấy danh sách QR Code từ DB để lưu vào Inbound
            List<BottleQrCode> bottleQrCodes = bottleQrCodeRepository.findAll();

            for (BottleQrCode bottleQrCode : bottleQrCodes) {
                // ✅ Lấy thông tin từ QR Code
                DeliveryDetail deliveryDetail = bottleQrCode.getDeliveryDetail();
                MasterDataDelivery masterDataDelivery = deliveryDetail.getMasterDataDelivery();
                Delivery delivery = masterDataDelivery.getDelivery();
                MasterData masterData = masterDataDelivery.getMasterData();

                // ✅ Tạo đối tượng Inbound với thông tin từ BottleQrCode
                Inbound inbound = Inbound.builder()
                        .importDate(LocalDate.now())
                        .delivery(delivery)
                        .supplier("Shell")
                        .item(masterData.getItem()) // Lấy từ MasterData
                        .itemName(masterData.getName())
                        .qrCode(bottleQrCode.getQrCode()) // Lấy QR Code từ entity
                        .manufacturingDate(masterDataDelivery.getManufaturingDate())
                        .expirationDate(masterDataDelivery.getExpirationDate())
                        .batch(masterDataDelivery.getBatch())
                        .user(currentUser)
                        .quantity(1)
                        .status(InboundStatus.ACTIVE)
                        .build();

                inbounds.add(inbound);
            }


            // 🔹 Lưu dữ liệu Inbound sau khi lấy từ QR Code
            if (!inbounds.isEmpty()) {
                inboundRepository.saveAll(inbounds);
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