package com.mt.shop.domain.model.address;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.shop.domain.DomainRegistry;
import org.springframework.stereotype.Service;

@Service
public class AddressValidationService {
    public void validate(Address address, ValidationNotificationHandler handler) {
        SumPagedRep<Address> addressPerUser = DomainRegistry.getAddressRepository().addressesOfQuery(new AddressQuery(false));
        validateMaxAddressPerUser(handler, addressPerUser);
        validateNoDuplicateAddressPerUser(address, handler, addressPerUser);
    }

    private void validateNoDuplicateAddressPerUser(Address address, ValidationNotificationHandler handler, SumPagedRep<Address> addressPerUser) {
        if (addressPerUser.getData().stream().anyMatch(e -> Address.isDuplicateOf(address, e))) {
            handler.handleError("same address can not be created more than one time");
        }
    }

    private void validateMaxAddressPerUser(ValidationNotificationHandler handler, SumPagedRep<Address> addressPerUser) {
        if (addressPerUser.getTotalItemCount() == 5L)
            handler.handleError("max 5 address reached");
    }
}
