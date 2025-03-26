package org.example.securetracks.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.CustomerMasterDataDTO;
import org.example.securetracks.model.CustomerMasterData;
import org.example.securetracks.model.User;
import org.example.securetracks.repository.CustomerMasterDataRepository;
import org.example.securetracks.repository.UserRepository;
import org.example.securetracks.service.ICustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final CustomerMasterDataRepository customerRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    public CustomerMasterData createCustomer(CustomerMasterDataDTO request) {
        if (customerRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Phone number already exists!");
        }

        CustomerMasterData customer = CustomerMasterData.builder()
                .phoneNumber(request.getPhoneNumber())
                .customerName(request.getCustomerName())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .street(request.getStreet())
                .addressDetail(request.getDetailAddress())
                .build();

        return customerRepository.save(customer);
    }
    public List<CustomerMasterDataDTO> getAllCustomers() {
        List<CustomerMasterData> customers = customerRepository.findAll();
        return customers.stream().map(this::mapDTO).collect(Collectors.toList());
    }

    public CustomerMasterDataDTO getCustomerByPhoneNumber(String phoneNumber) {
        Optional<CustomerMasterData> customer = customerRepository.findByPhoneNumber(phoneNumber);
        return customer.map(this::mapDTO).orElse(null);
    }
    private CustomerMasterDataDTO mapDTO(CustomerMasterData customer) {
        return CustomerMasterDataDTO.builder()
                .id(customer.getId())
                .phoneNumber(customer.getPhoneNumber())
                .customerName(customer.getCustomerName())
                .province(customer.getProvince())
                .district(customer.getDistrict())
                .ward(customer.getWard())
                .street(customer.getStreet())
                .detailAddress(customer.getAddressDetail())
                .build();
    }

    private CustomerMasterData mapEntity(CustomerMasterDataDTO dto) {
        return CustomerMasterData.builder()
                .id(dto.getId())
                .phoneNumber(dto.getPhoneNumber())
                .customerName(dto.getCustomerName())
                .province(dto.getProvince())
                .district(dto.getDistrict())
                .ward(dto.getWard())
                .street(dto.getStreet())
                .addressDetail(dto.getDetailAddress())
                .build();
    }

}
