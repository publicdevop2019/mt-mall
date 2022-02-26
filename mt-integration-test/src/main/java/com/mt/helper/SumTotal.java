package com.mt.helper;

import lombok.Data;

import java.util.List;
@Data
public class SumTotal<T> {
    private List<T> data;
    private Integer totalItemCount;
}
