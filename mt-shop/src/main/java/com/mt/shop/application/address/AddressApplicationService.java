package com.mt.shop.application.address;

import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.shop.application.ApplicationServiceRegistry;
import com.mt.shop.application.address.command.CustomerCreateAddressCommand;
import com.mt.shop.application.address.command.CustomerUpdateAddressCommand;
import com.mt.shop.domain.DomainRegistry;
import com.mt.shop.domain.model.address.Address;
import com.mt.shop.domain.model.address.AddressId;
import com.mt.shop.domain.model.address.AddressQuery;
import com.mt.shop.domain.model.biz_order.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AddressApplicationService {
    public static final String AGGREGATE_NAME = "Address";
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private PlatformTransactionManager transactionManager;

    public SumPagedRep<Address> addresses(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getAddressRepository().addressesOfQuery(new AddressQuery(queryParam, pageParam, skipCount, true));
    }

    public Optional<Address> address(String id) {
        return DomainRegistry.getAddressRepository().addressesOfQuery(new AddressQuery(new AddressId(id), true)).findFirst();
    }

    public Optional<Address> addressForUser(String id) {
        return DomainRegistry.getAddressRepository().addressesOfQuery(new AddressQuery(new AddressId(id), false)).findFirst();
    }

    public SumPagedRep<Address> addresses() {
        return DomainRegistry.getAddressRepository().addressesOfQuery(new AddressQuery(false));
    }

    @SubscribeForEvent
    public String create(CustomerCreateAddressCommand command, String changeId) {
        RLock lock = redissonClient.getLock(UserThreadLocal.get() + "_address");
        lock.lock(5, TimeUnit.SECONDS);
        log.debug("lock acquired");
        try {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            String bizCart = template.execute(new TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    return ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId,
                            (change) -> {
                                AddressId addressId = new AddressId();
                                Address address = Address.create(addressId, command);
                                DomainRegistry.getAddressRepository().add(address);
                                change.setReturnValue(addressId.toString());
                                return addressId.toString();
                            }, AGGREGATE_NAME
                    );
                }
            });
            return bizCart;
        } finally {
            log.debug("release lock");
            lock.unlock();
        }
    }

    @SubscribeForEvent
    @Transactional
    public void replace(CustomerUpdateAddressCommand command, String id, String changeId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (change) -> {
            AddressId addressId = new AddressId(id);
            Optional<Address> optionalAddress = DomainRegistry.getAddressRepository().addressesOfQuery(new AddressQuery(addressId, false)).findFirst();
            optionalAddress.ifPresent(e -> {
                Address address = optionalAddress.get();
                address.replace(command);
            });
            return null;
        }, AGGREGATE_NAME);
    }

    @SubscribeForEvent
    @Transactional
    public void remove(String id, String changeId) {
        ApplicationServiceRegistry.getIdempotentWrapper().idempotent(changeId, (change) -> {
            AddressId addressId = new AddressId(id);
            DomainRegistry.getAddressRepository().addressesOfQuery(new AddressQuery(addressId, false)).findFirst()
                    .ifPresent(address -> DomainRegistry.getAddressRepository().remove(address));
            return null;
        }, AGGREGATE_NAME);
    }
}
