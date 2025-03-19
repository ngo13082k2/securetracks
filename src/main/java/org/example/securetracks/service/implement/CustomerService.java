package org.example.securetracks.service.implement;

import lombok.RequiredArgsConstructor;
import org.example.securetracks.dto.CustomerMasterDataDTO;
import org.example.securetracks.model.CustomerMasterData;
import org.example.securetracks.model.User;
import org.example.securetracks.repository.CustomerMasterDataRepository;
import org.example.securetracks.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

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

    public List<CustomerMasterData> getCustomersByLoggedInUser() {
        User user = userService.getCurrentUser();
        return customerRepository.findByUser(user);
    }
}
