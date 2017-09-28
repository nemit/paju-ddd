package services

import model.Product
import model.Reservation

fun listProducts(): List<Product> {
    return listOf(Product("Majoitus"), Product("Ruokailu"), Product("Keilailu"), Product("Hieronta"))
}