package com.mt.user_profile.domain.biz_order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BizOrderItemAddOnSelection implements Serializable {

    private static final long serialVersionUID = 1;

    private String optionValue;

    private String priceVar;
}
