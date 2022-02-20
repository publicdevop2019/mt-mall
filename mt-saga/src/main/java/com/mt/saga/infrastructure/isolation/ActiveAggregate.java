package com.mt.saga.infrastructure.isolation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Data
@NoArgsConstructor
public class ActiveAggregate {
    @Id
    @Setter
    private String aggregateId;

    public ActiveAggregate(String orderId) {
        setAggregateId(orderId);
    }
}