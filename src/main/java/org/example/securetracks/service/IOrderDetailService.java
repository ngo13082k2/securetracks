package org.example.securetracks.service;

import org.example.securetracks.dto.OrderDetailDTO;
import org.example.securetracks.dto.OrderRequestDTO;
import org.example.securetracks.model.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrder(OrderRequestDTO orderRequest);
    List<OrderDetailDTO> getOrdersByUser();
    OrderDetailDTO getOrderDetailById(Long orderDetailId);
}
