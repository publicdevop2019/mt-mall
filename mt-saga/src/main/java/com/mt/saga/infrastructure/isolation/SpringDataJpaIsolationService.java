package com.mt.saga.infrastructure.isolation;

import com.mt.saga.domain.model.IsolationService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * concurrent scenario:
 * this only works fine when consumer only trying to update same db,
 * switch to redisson isolation when consumer do more than db update e.g. api call
 *
 */
@Primary
@Slf4j
@Service
public class SpringDataJpaIsolationService implements IsolationService {
    private final SpringDataJpaActiveAggregateRepository repository;

    public SpringDataJpaIsolationService(SpringDataJpaActiveAggregateRepository repository) {
        this.repository = repository;
    }
    @Override
    public void removeActiveDtx(String lookUpId) {
        Optional<ActiveAggregate> byId = repository.getById(lookUpId);
        if(byId.isEmpty()){
            throw new IllegalArgumentException("unable to find active dtx with id "+lookUpId);
        }else {
            log.debug("releasing lock {} after dtx success",lookUpId);
            repository.remove(byId.get());
        }
    }

    @Override
    public void hasNoActiveDtx(Consumer<Void> consumer, String lookUpId) {
        Optional<ActiveAggregate> byId = repository.getById(lookUpId);
        if(byId.isEmpty()){
            consumer.accept(null);
            log.debug("add lock for {} after dtx started",lookUpId);
            repository.add(new ActiveAggregate(lookUpId));
        }else {
            log.debug("ignored bcz target {} has active business dtx",lookUpId);
        }
    }
}
