package com.mt.saga.domain.model.distributed_tx;


import com.mt.common.domain.model.sql.converter.EnumConverter;

public enum DTXStatus {
    PENDING,
    STARTED,
    RESOLVED,
    SUCCESS;

    public static class DBConverter extends EnumConverter<DTXStatus> {
        public DBConverter() {
            super(DTXStatus.class);
        }
    }
}
