package io.paju.salesorder.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.state.ProductState
import io.paju.salesorder.domain.state.SalesOrderState
import io.paju.salesorder.infrastructure.dao.ProductDao
import io.paju.salesorder.infrastructure.dao.SalesOrderDao

class SalesOrderStoreJdbc(
    private val salesOrderDao: SalesOrderDao,
    private val productDao: ProductDao
) : SalesOrderStore() {

    override fun getSalesOrderWithoutRelations(id: AggregateRootId): SalesOrderState {
        return salesOrderDao.getById(id).get()
    }

    override fun getProducts(id: AggregateRootId): List<ProductState> {
        return productDao.getBySalesOrderId(id)
    }

    override fun getProduct(id: AggregateRootId, product: Product): ProductState {
        return productDao.getById(id, product.id).get()
    }

    override fun add(id: AggregateRootId, product: ProductState) {
        productDao.insert(id, product)
    }

    override fun update(id: AggregateRootId, product: ProductState) {
        productDao.update(id, product)
    }

    override fun add(salesOrder: SalesOrderState) {
        salesOrderDao.insert(salesOrder)
    }

    override fun update(salesOrder: SalesOrderState) {
        salesOrderDao.update(salesOrder)
    }

    @Suppress("UNUSED_PARAMETER")
    override fun saveSnapshot(salesOrder: SalesOrderState){
        // not implemented
    }

}
