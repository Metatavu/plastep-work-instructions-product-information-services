package fi.metatavu.plastep.productinformation.lemon

import fi.metatavu.plastep.lemon.client.models.*
import org.slf4j.Logger
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Controller for Lemonsoft products
 */
@ApplicationScoped
class LemonProductsController {

    @Inject
    lateinit var lemonClient: LemonClient

    /**
     * Find product from Lemonsoft
     *
     * @param productId product id
     * @return product find response
     */
    fun findProduct(
        productId: Int
    ): GetProductResult? {
        return lemonClient.findProduct(
            productId = productId
        )
    }

    /**
     * Lists products from Lemonsoft
     *
     * @param page page number. Page number starts from 1
     * @param pageSize page size.
     * @param filterSku sku filter
     * @return list of products response
     */
    fun listProducts(
        page: Int,
        pageSize: Int,
        filterSku: Array<String>? = null
    ): ProductListResult? {
        return lemonClient.listProducts(
            filterPage = page,
            filterPageSize = pageSize,
            filterSku = filterSku
        )
    }

    /**
     * Returns product's product structure from Lemonsoft REST API. Result contains only single level
     *
     * @param productCode product code
     * @return product structure response
     */
    fun getDefaultProductStructure(productCode: String): GetProductStructureResult? {
        return lemonClient.getProductStructure(
            productCode = productCode,
            workNumber = 0,
            level = 1
        )
    }

}