package io.paju.templateservice.model.product

import org.omg.CORBA.Current

/**

 */

data class Price(val price: Float, val vat: Vat, val currency: Currencies = Currencies.EURO) {

}

enum class Vat {
    vat0, vat10, vat22, vat24
}

enum class Currencies {
    EURO
}