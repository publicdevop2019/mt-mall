package com.mt.saga.infrastructure.isolation;

import com.mt.saga.domain.model.IsolationService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.function.Consumer;
/**
 * concurrent scenario:
 * lock needs to be released after consumer is success e.g. db is flushed successfully
 * we may need to monitor lock status when lock not released successfully
 *
 */
@Slf4j
@Service
public class RedissonIsolationService implements IsolationService {
    private final RedissonClient redissonClient;

    public RedissonIsolationService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
    @Override
    public void removeActiveDtx(String lookUpId) {
        RBucket<Object> bucket = redissonClient.getBucket("dtx_" + lookUpId);
        boolean delete = bucket.delete();
        if (delete) {
            log.debug("releasing lock {} after dtx success", lookUpId);

        } else {
            throw new IllegalArgumentException("unable to find lock with id " + lookUpId);
        }
    }

    @Override
    public void hasNoActiveDtx(Consumer<Void> consumer, String lookUpId) {
        RBucket<Object> bucket = redissonClient.getBucket("dtx_" + lookUpId);
        boolean b = bucket.trySet(Date.from(Instant.now()));
        if (b) {
            log.debug("add lock for {} after dtx started", lookUpId);
            consumer.accept(null);
        } else {
            log.debug("ignored bcz target {} has active business dtx", lookUpId);
        }
    }
}
