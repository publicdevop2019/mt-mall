package com.mt.shop.domain.model.product.event;

import com.mt.common.domain.model.restful.PatchCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
public class ProductPatchBatched extends ProductEvent {
    private List<PatchCommand> patchCommands;

    public ProductPatchBatched(List<PatchCommand> patchCommands) {
        super();
        this.patchCommands = patchCommands;
    }
}
