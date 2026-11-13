package fi.metatavu.plastep.productinformation.products

import fi.metatavu.plastep.lemon.client.models.GetProductResult
import fi.metatavu.plastep.productinformation.model.ProductFindResponse
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Translates lemon product find response to integration REST product find response
 */
@ApplicationScoped
class LemonProductFindTranslator :
    AbstractTranslator<GetProductResult, ProductFindResponse>() {

    @Inject
    lateinit var lemonProductTranslator: LemonProductTranslator

    override fun translate(entity: GetProductResult): ProductFindResponse {
        return ProductFindResponse(
            product = entity.result?.let(lemonProductTranslator::translate),
            hasErrors = entity.hasErrors,
            ok = entity.ok
        )
    }

}
