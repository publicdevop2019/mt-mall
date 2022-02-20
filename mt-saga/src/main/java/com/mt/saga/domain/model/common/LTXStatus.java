package com.mt.saga.domain.model.common;


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
