package org.example.securetracks.service;

import org.example.securetracks.dto.CustomerMasterDataDTO;
import org.example.securetracks.model.CustomerMasterData;

public interface ICustomerService {
    CustomerMasterData createCustomer(CustomerMasterDataDTO request);
}
