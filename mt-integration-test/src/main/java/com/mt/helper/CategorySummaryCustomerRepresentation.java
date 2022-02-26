package com.mt.helper;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CategorySummaryCustomerRepresentation {
    private List<CategorySummaryCardRepresentation> data;
}
