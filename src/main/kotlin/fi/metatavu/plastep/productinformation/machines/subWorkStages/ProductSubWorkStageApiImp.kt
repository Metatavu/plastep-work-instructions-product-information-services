package fi.metatavu.plastep.productinformation.machines.subWorkStages

import fi.metatavu.plastep.productinformation.lemon.LemonProductsController
import fi.metatavu.plastep.productinformation.lemon.LemonWorkStagesController
import fi.metatavu.plastep.productinformation.model.WorkStageState
import fi.metatavu.plastep.productinformation.rest.AbstractApi
import fi.metatavu.plastep.productinformation.spec.ProductSubWorkStagesApi
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response

@RequestScoped
class ProductSubWorkStageApiImp: ProductSubWorkStagesApi, AbstractApi() {

    @Inject
    lateinit var productSubWorkStageTranslator: ProductSubWorkStageTranslator

    @Inject
    lateinit var lemonWorkStagesController: LemonWorkStagesController

    @Inject
    lateinit var lemonProductsController: LemonProductsController

    override fun listSubWorkStages(machineCode: String?, state: WorkStageState?): Response {
        if (!hasWorkerRole() && !hasAdminRole()) {
            return createForbidden("Only user or admin is allowed to access this resource")
        }

        val workStages = lemonWorkStagesController.listSubWorkStages(
            machineCode = machineCode,
            state = state
        )

        val allProducts = lemonProductsController.listProducts(
            filterSku = workStages.map { it.productCode }.distinct().toTypedArray(),
            pageSize = workStages.size,
            page = 1
        )

        val productsWithWorkStages = workStages.map { workStage ->
            val product = allProducts?.results?.find { it.sku == workStage.productCode }
            WorkStagesAndProducts(workStage, product)
        }
        return createOk(productSubWorkStageTranslator.translate(productsWithWorkStages))
    }
}