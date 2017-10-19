package io.paju.templateservice.domain.product

data class Price(val price:Float, val vat: Vat, val currency: Currencies = Currencies.EURO) {

}

enum class Vat {
    vat0, vat10, vat22, vat24, undefined
}

enum class Currencies {
    EURO
}

fun vatFromString(vat: String): Vat {
    return when(vat) {
        "vat0" -> Vat.vat0
        "vat10" -> Vat.vat10
        "vat22" -> Vat.vat22
        "vat24" -> Vat.vat24
        else -> Vat.undefined
    }
}