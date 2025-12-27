package com.lgzarturo.common.dto.products;

import com.lgzarturo.common.dto.common.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceChange {
    private Currency currency = Currency.MXN;
    private BigDecimal amount;
    private ValueType valueType;
    private PriceChangeType type;
}
