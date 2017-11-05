package io.paju.salesorder.domain

import java.math.BigDecimal

data class Price(val price: BigDecimal, val vat: Vat, val currency: Currencies = Currencies.EURO)

enum class Vat {
    vat0, vat10, vat22, vat24, undefined;
}


enum class Currencies {
    EURO
}