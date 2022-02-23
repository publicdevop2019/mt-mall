package com.mt.saga.domain.model.distributed_tx;


import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum LTXStatus {
    PENDING,
    STARTED,
    SUCCESS;

    public static class DBConverter extends EnumConverter<LTXStatus> {
        public DBConverter() {
            super(LTXStatus.class);
        }
    }
}
