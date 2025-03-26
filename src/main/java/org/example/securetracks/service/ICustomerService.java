package org.example.securetracks.service;

import org.example.securetracks.dto.CustomerMasterDataDTO;
import org.example.securetracks.model.CustomerMasterData;

import java.util.List;

public interface ICustomerService {
    CustomerMasterData createCustomer(CustomerMasterDataDTO request);
    CustomerMasterDataDTO getCustomerByPhoneNumber(String phoneNumber);
    List<CustomerMasterDataDTO> getAllCustomers();
}
