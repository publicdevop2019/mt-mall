package com.mt.shop.application.sku.command;

import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.shop.domain.model.product.event.ProductEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InternalSkuPatchCommand extends ProductEvent {
    private List<PatchCommand> skuCommands;
    private String changeId;
    private String orderId;
    private long taskId;
}
