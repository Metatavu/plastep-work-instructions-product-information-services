package fi.metatavu.plastep.productinformation.products

import fi.metatavu.plastep.lemon.client.models.ProductStructureNode
import fi.metatavu.plastep.productinformation.lemon.LemonProductsController
import fi.metatavu.plastep.productinformation.model.Product
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Translates Lemon product to REST product
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class LemonProductTranslator : AbstractTranslator<fi.metatavu.plastep.lemon.client.models.Product, Product>() {

    @Inject
    lateinit var lemonProductsController: LemonProductsController

    override fun translate(entity: fi.metatavu.plastep.lemon.client.models.Product): Product {
        val structure = lemonProductsController.getDefaultProductStructure(productCode = entity.sku)
        val childProducts = if (structure?.ok == true) {
            structure.result?.nodes?.mapNotNull(ProductStructureNode::node)
        } else null

        return Product(
            id = entity.id,
            name = entity.name,
            productCode = entity.sku,
            childProductCodes = childProducts?.mapNotNull { it.productCode } ?: emptyList(),
            childProductIds = childProducts?.mapNotNull { it.productId } ?: emptyList(),
        )
    }

}
