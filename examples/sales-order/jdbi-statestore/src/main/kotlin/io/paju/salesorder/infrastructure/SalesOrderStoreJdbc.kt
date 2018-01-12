package io.paju.salesorder.infrastructure

import io.paju.ddd.AggregateRootId
import io.paju.ddd.EntityId
import io.paju.salesorder.domain.Product
import io.paju.salesorder.domain.state.ProductState
import io.paju.salesorder.domain.state.SalesOrderState
import io.paju.salesorder.infrastructure.dao.ProductDao
import io.paju.salesorder.infrastructure.dao.SalesOrderDao
import org.jdbi.v3.core.Jdbi

class SalesOrderStoreJdbc(private val salesOrderDao: SalesOrderDao, private val productDao: ProductDao) : SalesOrderStore() {

    constructor(jdbi: Jdbi):
        this(SalesOrderDao(jdbi), ProductDao(jdbi))

    constructor(jdbcUrl: String): this(Jdbi.create(jdbcUrl))

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

    override fun remove(id: AggregateRootId, productId: EntityId) {
        productDao.delete(id, productId)
    }

    override fun update(id: AggregateRootId, product: ProductState) {
        productDao.update(id, product)
    }

    override fun create(id: AggregateRootId) {
        salesOrderDao.create(id)
    }

    override fun update(id: AggregateRootId, salesOrder: SalesOrderState) {
        salesOrderDao.update(id, salesOrder)
    }
}
