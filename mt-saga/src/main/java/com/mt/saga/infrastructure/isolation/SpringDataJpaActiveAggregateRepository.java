package com.mt.saga.infrastructure.isolation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataJpaActiveAggregateRepository extends JpaRepository<ActiveAggregate, String>{

    default void add(ActiveAggregate aggregate) {
        save(aggregate);
    }

    default void remove(ActiveAggregate id) {
        delete(id);
    }

    default Optional<ActiveAggregate> getById(String id) {
        return findById(id);
    }

}