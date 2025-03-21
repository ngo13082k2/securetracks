package org.example.securetracks.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.OrderDetailDTO;
import org.example.securetracks.dto.OrderQrDetailDTO;
import org.example.securetracks.dto.OrderRequestDTO;
import org.example.securetracks.model.*;
import org.example.securetracks.repository.BottleQrCodeRepository;
import org.example.securetracks.repository.CustomerMasterDataRepository;
import org.example.securetracks.repository.OrderDetailRepository;
import org.example.securetracks.repository.OrderQrDetailRepository;
import org.example.securetracks.response.BottleQrCodeResponse;
import org.example.securetracks.service.IOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public OrderDetail createOrder(OrderRequestDTO request) {
        // Lấy User đang đăng nhập
        User currentUser = userService.getCurrentUser();

        CustomerMasterData customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElse(null);

        if (customer == null) {
            // Nếu khách hàng chưa tồn tại, tạo mới và gán User
            customer = CustomerMasterData.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .customerName(request.getCustomerName())
                    .province(request.getProvince())
                    .district(request.getDistrict())
                    .ward(request.getWard())
                    .street(request.getStreet())
                    .addressDetail(request.getAddressDetail())
                    .user(currentUser) // Gán User vào Customer
                    .build();
        } else {
            // Nếu khách hàng đã tồn tại, cập nhật thông tin mới và gán User
            customer.setCustomerName(request.getCustomerName());
            customer.setProvince(request.getProvince());
            customer.setDistrict(request.getDistrict());
            customer.setWard(request.getWard());
            customer.setStreet(request.getStreet());
            customer.setAddressDetail(request.getAddressDetail());
            customer.setUser(currentUser); // Gán User vào Customer
        }

        customerRepository.save(customer);

        // Tạo đơn hàng và gán User
        OrderDetail order = OrderDetail.builder()
                .customer(customer)
                .user(currentUser) // Gán User vào OrderDetail
                .totalProducts(request.getQrCodes().size())
                .dateCreate(LocalDateTime.now())
                .build();
        orderDetailRepository.save(order);

        // Lưu từng QR code vào OrderQrDetail
        List<OrderQrDetail> qrDetails = request.getQrCodes().stream().map(qrCode ->
                OrderQrDetail.builder()
                        .qrCode(qrCode)
                        .orderCreationDate(LocalDateTime.now())
                        .orderDetail(order)
                        .build()
        ).collect(Collectors.toList());

        orderQrDetailRepository.saveAll(qrDetails);

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
    public List<OrderDetailDTO> searchOrders(String phoneNumber, LocalDate orderDate) {
        User currentUser = userService.getCurrentUser();

        List<OrderDetail> orders;
        if (phoneNumber != null && orderDate != null) {
            orders = orderDetailRepository.findByUserAndCustomerPhoneNumberAndDateCreate(currentUser, phoneNumber, orderDate);
        } else if (phoneNumber != null) {
            orders = orderDetailRepository.findByUserAndCustomerPhoneNumber(currentUser, phoneNumber);
        } else if (orderDate != null) {
            orders = orderDetailRepository.findByUserAndDateCreate(currentUser, orderDate);
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


