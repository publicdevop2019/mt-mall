package com.mt.saga.appliction.common;

import com.mt.saga.domain.model.common.DTXStatus;
import com.mt.saga.domain.model.common.LTXStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class CommonDTXRepresentation {
    protected Long id;
    protected DTXStatus status;
    protected String changeId;
    protected String orderId;
    protected long createdAt;
    protected Map<String, LTXStatus> statusMap = new HashMap<>();
    protected Map<String, Boolean> emptyOptMap = new HashMap<>();
    protected Map<String, Long> idMap = new HashMap<>();
}
