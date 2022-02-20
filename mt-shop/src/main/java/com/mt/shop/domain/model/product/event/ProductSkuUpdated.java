package com.mt.shop.domain.model.product.event;

import com.mt.shop.application.sku.command.CreateSkuCommand;
import com.mt.shop.application.sku.command.UpdateSkuCommand;
import com.mt.shop.domain.model.product.ProductId;
import com.mt.shop.domain.model.sku.SkuId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
public class ProductSkuUpdated extends ProductEvent {
    private List<CreateSkuCommand> createSkuCommands;
    private List<UpdateSkuCommand> updateSkuCommands;
    private Set<SkuId> removeSkuCommands;
    private String changeId;

    public ProductSkuUpdated(ProductId productId, List<CreateSkuCommand> createSkuCommands, List<UpdateSkuCommand> updateSkuCommands, Set<SkuId> removeSkuCommands, String changeId) {
        super(productId);
        this.createSkuCommands = createSkuCommands;
        this.updateSkuCommands = updateSkuCommands;
        this.removeSkuCommands = removeSkuCommands;
        this.changeId = changeId;
    }
}
