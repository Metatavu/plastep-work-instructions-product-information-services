package fi.metatavu.plastep.productinformation.products

import fi.metatavu.plastep.lemon.client.models.ProductListResult
import fi.metatavu.plastep.productinformation.model.ProductsListResponse
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Translates Lemon result of product listing to integeation service product list response
 */
@ApplicationScoped
class LemonProductListTranslator :
    AbstractTranslator<ProductListResult?, ProductsListResponse>() {

    @Inject
    lateinit var lemonProductTranslator: LemonProductTranslator

    override fun translate(entity: ProductListResult?): ProductsListResponse {
        val products = entity?.results?.map(lemonProductTranslator::translate) ?: emptyList()

        return ProductsListResponse(
            products = products,
            hasErrors = entity?.hasErrors ?: true,
            hasNextPage = entity?.hasNextPage ?: false
        )
    }

}