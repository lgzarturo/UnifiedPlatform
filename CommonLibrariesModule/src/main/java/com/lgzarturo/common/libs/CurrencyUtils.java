package com.lgzarturo.common.libs;


import java.math.BigDecimal;

import static com.lgzarturo.common.libs.Constants.*;

public class CurrencyUtils {
    public static String getCurrencySymbol(String currency) {
        currency = currency.toUpperCase();
        return switch (currency) {
            case CURRENCY_MXN -> CURRENCY_MXN_SYMBOL;
            case CURRENCY_USD -> CURRENCY_USD_SYMBOL;
            case CURRENCY_EUR -> CURRENCY_EUR_SYMBOL;
            default -> "";
        };
    }

    public static String getFormattedPrice(String currency, BigDecimal amount) {
        var priceValue = amount != null ? amount : BigDecimal.ZERO;
        return getCurrencySymbol(currency) + priceValue + " (" + currency + ")";
    }
}
