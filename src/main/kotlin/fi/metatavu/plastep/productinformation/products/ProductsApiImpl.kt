package fi.metatavu.plastep.productinformation.products

import fi.metatavu.plastep.productinformation.rest.AbstractApi
import fi.metatavu.plastep.productinformation.lemon.LemonProductsController
import fi.metatavu.plastep.productinformation.spec.ProductsApi
import org.slf4j.Logger
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.core.Response

@RequestScoped
@Suppress ("UNUSED")
@Consumes ("application/json")
class ProductsApiImpl: ProductsApi, AbstractApi() {

    @Inject
    lateinit var lemonProductsController: LemonProductsController

    @Inject
    lateinit var lemonProductListTranslator: LemonProductListTranslator

    @Inject
    lateinit var lemonProductFindTranslator: LemonProductFindTranslator

    @Inject
    lateinit var logger: Logger

    override fun findProduct(id: Int): Response {
        if (!hasIntegrationRole()) {
            return createForbidden("Only integration users can access this resource")
        }

        val lemonProduct = lemonProductsController.findProduct(id) ?: return createNotFound("Product $id not found")
        return createOk(lemonProductFindTranslator.translate(lemonProduct))
    }

    override fun listProducts(page: Int?, pageSize: Int?): Response {
        if (!hasIntegrationRole()) {
            return createForbidden("Only integration users can access this resource")
        }

        val lemonProducts = lemonProductsController.listProducts(
            page ?: 1,
            pageSize ?: 10
        )

        return createOk(lemonProductListTranslator.translate(lemonProducts))
    }

}