package com.lgzarturo.common.dto.products;


import com.lgzarturo.common.dto.common.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PriceRequest {
    private Currency currency = Currency.MXN;
    private BigDecimal amount;
}
