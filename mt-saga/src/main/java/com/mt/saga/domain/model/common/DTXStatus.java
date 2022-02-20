package com.mt.saga.domain.model.common;


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
