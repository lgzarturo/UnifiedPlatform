package com.lgzarturo.common.dto.products;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PriceChange {
    private BigDecimal value;
    private ValueType valueType;
    private PriceChangeType type;
}
