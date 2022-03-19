package com.mt.shop.port.adapter.persistence.address;

import com.mt.common.domain.model.audit.Auditable_;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.shop.domain.model.address.Address;
import com.mt.shop.domain.model.address.AddressQuery;
import com.mt.shop.domain.model.address.AddressRepository;
import com.mt.shop.domain.model.address.Address_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Order;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Repository
public interface SpringDataJpaAddressRepository extends JpaRepository<Address, Long>, AddressRepository {

    default void add(Address address) {
        save(address);
    }

    default void remove(Address address) {
        address.softDelete();
        save(address);
    }

    default SumPagedRep<Address> addressesOfQuery(AddressQuery addressQuery) {
        QueryUtility.QueryContext<Address> queryContext = QueryUtility.prepareContext(Address.class, addressQuery);
        Optional.ofNullable(addressQuery.getUserId()).ifPresent(e -> QueryUtility.addStringEqualPredicate(e, Auditable_.CREATED_BY, queryContext));
        Order order = null;
        if (addressQuery.getAddressSort().isById())
            order = QueryUtility.getDomainIdOrder(Address_.ADDRESS_ID, queryContext, addressQuery.getAddressSort().isAsc());
        queryContext.setOrder(order);
        return QueryUtility.pagedQuery(addressQuery, queryContext);
    }

}
