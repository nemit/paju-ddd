package io.paju.templateservice.model.product

data class Price(val price:Float , val vat: Vat, val currency: Currencies = Currencies.EURO) {

}

enum class Vat {
    vat0, vat10, vat22, vat24
}

enum class Currencies {
    EURO
}