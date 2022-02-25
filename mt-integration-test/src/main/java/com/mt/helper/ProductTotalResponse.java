package com.mt.helper;

import lombok.Data;

import java.util.List;

@Data
public class ProductTotalResponse {
    public List<ProductSimple> data;
    public Integer totalItemCount;
}
