package com.mt.user_profile.domain.address;

import com.mt.common.domain.model.restful.SumPagedRep;

public interface AddressRepository {
    SumPagedRep<Address> addressesOfQuery(AddressQuery addressQuery);

    void add(Address address);

    void remove(Address address);

}
