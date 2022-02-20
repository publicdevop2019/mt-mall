package com.mt.saga.appliction.common;

import com.mt.saga.domain.model.common.DTXStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommonDTXCardRepresentation {
    protected Long id;
    protected DTXStatus status;
    protected String changeId;
    protected String orderId;
    protected long createdAt;
}
