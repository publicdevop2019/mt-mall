package com.mt.user_profile.domain.biz_order;

import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class SkuDetail {
    private Map<String, Integer> skuInfo;//skuId and amount
    @Setter
    private SkuStatus orderSkuStatus = SkuStatus.RESERVED;
    @Setter
    private SkuStatus actualSkuStatus = SkuStatus.PENDING;

    public SkuDetail(Map<String, Integer> skuInfo) {
        setSkuInfo(skuInfo);
    }

    public enum SkuStatus {
        RESERVED,
        PENDING;
    }

    public static SkuDetail parse(Set<CartDetail> collect) {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        collect.forEach(e -> stringIntegerHashMap.merge(e.getSkuId(), e.getAmount(), Integer::sum));
        Map<String, Integer> readOnlyMap = Collections.unmodifiableMap(stringIntegerHashMap);
        return new SkuDetail(readOnlyMap);
    }

    public void setSkuInfo(Map<String, Integer> skuInfo) {
        Validator.notNull(skuInfo);
        Validator.notEmpty(skuInfo.keySet());
        this.skuInfo = skuInfo;
    }
}
