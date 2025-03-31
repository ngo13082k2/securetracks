package org.example.securetracks.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.OrderDetailDTO;
import org.example.securetracks.dto.OrderQrDetailDTO;
import org.example.securetracks.dto.OrderRequestDTO;
import org.example.securetracks.model.*;
import org.example.securetracks.model.enums.InboundStatus;
import org.example.securetracks.repository.*;
import org.example.securetracks.response.BottleQrCodeResponse;
import org.example.securetracks.service.IOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService {

    private final CustomerMasterDataRepository customerRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderQrDetailRepository orderQrDetailRepository;
    private final UserService userService;
    private final BottleQrCodeService bottleQrCodeService;
    private final OutBoundRepository outBoundRepository;
    private final InboundRepository inboundRepository;
    public OrderDetail createOrder(OrderRequestDTO request) {
        User currentUser = userService.getCurrentUser();

        CustomerMasterData customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElse(null);

        if (customer == null) {
            customer = CustomerMasterData.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .customerName(request.getCustomerName())
                    .province(request.getProvince())
                    .district(request.getDistrict())
                    .ward(request.getWard())
                    .street(request.getStreet())
                    .addressDetail(request.getAddressDetail())
                    .build();
        } else {
            customer.setCustomerName(request.getCustomerName());
            customer.setProvince(request.getProvince());
            customer.setDistrict(request.getDistrict());
            customer.setWard(request.getWard());
            customer.setStreet(request.getStreet());
            customer.setAddressDetail(request.getAddressDetail());
        }

        customerRepository.save(customer);

        List<String> qrCodes = request.getQrCodes();
        List<OrderQrDetail> existingQrDetails = orderQrDetailRepository.findByQrCodeIn(qrCodes);

        if (!existingQrDetails.isEmpty()) {
            List<String> existingQrCodes = existingQrDetails.stream()
                    .map(OrderQrDetail::getQrCode)
                    .collect(Collectors.toList());
            throw new RuntimeException("Các QR Codes đã tồn tại: " + existingQrCodes);
        }

        OrderDetail order = OrderDetail.builder()
                .customer(customer)
                .user(currentUser)
                .totalProducts(qrCodes.size())
                .dateCreate(LocalDate.now())
                .timeCreate(LocalTime.now())
                .build();
        orderDetailRepository.save(order);

        List<OrderQrDetail> qrDetails = qrCodes.stream().map(qrCode ->
                OrderQrDetail.builder()
                        .qrCode(qrCode)
                        .orderCreationDate(LocalDateTime.now())
                        .orderDetail(order)
                        .build()
        ).collect(Collectors.toList());
        orderQrDetailRepository.saveAll(qrDetails);

        // Lưu vào Outbound
        CustomerMasterData finalCustomer = customer;
        List<OutBound> outbounds = qrDetails.stream().map(qrDetail -> {
            BottleQrCodeResponse bottleInfo = bottleQrCodeService.getBottleInfoByQrCode(qrDetail.getQrCode());
            return OutBound.builder()
                    .orderId(order.getId())
                    .saleDate(LocalDate.now())
                    .qrcode(qrDetail.getQrCode())
                    .customerName(finalCustomer.getCustomerName())
                    .phoneNumber(finalCustomer.getPhoneNumber())
                    .item(bottleInfo.getMasterDataId())
                    .itemName(bottleInfo.getMasterDataName())
                    .manufacturingDate(bottleInfo.getManufacturingDate())
                    .expirationDate(bottleInfo.getExpirationDate())
                    .batch(bottleInfo.getBatch())
                    .dealer(currentUser.getUsername())
                    .quantity(1)
                    .build();
        }).collect(Collectors.toList());

        outBoundRepository.saveAll(outbounds);
        List<Inbound> inboundsToUpdate = inboundRepository.findByQrCodeIn(qrCodes);
        for (Inbound inbound : inboundsToUpdate) {
            inbound.setStatus(InboundStatus.SOLD);
        }
        inboundRepository.saveAll(inboundsToUpdate);

        return order;
    }


    public List<OrderDetailDTO> getOrdersByUser() {
        User currentUser = userService.getCurrentUser();

        List<OrderDetail> orders = orderDetailRepository.findByUser(currentUser);

        return orders.stream().map(order -> OrderDetailDTO.builder()
                .id(order.getId())
                .totalProducts(order.getTotalProducts())
                .customerPhoneNumber(order.getCustomer().getPhoneNumber()) // Lấy số điện thoại
                .customerName(order.getCustomer().getCustomerName())
                .orderDate(order.getDateCreate())
                .build()
        ).collect(Collectors.toList());
    }
    public OrderDetailDTO getOrderDetailById(Long orderDetailId) {
        OrderDetail order = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Lấy danh sách QR Code
        List<OrderQrDetailDTO> qrDetails = order.getQrDetails().stream().map(qr -> {
            // Lấy thông tin chi tiết QR Code từ BottleQrCodeService
            BottleQrCodeResponse bottleInfo = bottleQrCodeService.getBottleInfoByQrCode(qr.getQrCode());

            return OrderQrDetailDTO.builder()
                    .id(qr.getId())
                    .qrCode(qr.getQrCode())
                    .orderCreationDate(qr.getOrderCreationDate())
                    .bottleInfo(bottleInfo) // Gán thông tin từ BottleQrCodeResponse
                    .build();
        }).collect(Collectors.toList());

        return OrderDetailDTO.builder()
                .id(order.getId())
                .totalProducts(order.getTotalProducts())
                .customerPhoneNumber(order.getCustomer().getPhoneNumber())
                .customerName(order.getCustomer().getCustomerName())
                .qrDetails(qrDetails)
                .build();
    }
    public List<OrderDetailDTO> searchOrders(String phoneNumber, LocalDate startDate, LocalDate endDate) {
        User currentUser = userService.getCurrentUser();
        List<OrderDetail> orders;

        if (startDate != null && endDate != null) {
            orders = orderDetailRepository.findByUserAndDateCreateBetween(currentUser, startDate, endDate);
        } else if (phoneNumber != null) {
            orders = orderDetailRepository.findByUserAndCustomerPhoneNumber(currentUser, phoneNumber);
        } else {
            orders = orderDetailRepository.findByUser(currentUser);
        }

        return orders.stream().map(order -> OrderDetailDTO.builder()
                .id(order.getId())
                .totalProducts(order.getTotalProducts())
                .customerPhoneNumber(order.getCustomer().getPhoneNumber())
                .customerName(order.getCustomer().getCustomerName())
                .orderDate(order.getDateCreate())
                .build()
        ).collect(Collectors.toList());
    }


}


