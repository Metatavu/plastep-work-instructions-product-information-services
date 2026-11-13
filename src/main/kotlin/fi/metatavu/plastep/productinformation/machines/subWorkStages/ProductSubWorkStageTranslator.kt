package fi.metatavu.plastep.productinformation.machines.subWorkStages

import fi.metatavu.plastep.lemon.client.models.Product
import fi.metatavu.plastep.lemon.client.models.SubWorkStage
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import fi.metatavu.plastep.productinformation.workstages.LemonWorkStagesTranslator
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Translator for Lemonsoft product sub work stages
 */
@ApplicationScoped
class ProductSubWorkStageTranslator : AbstractTranslator<WorkStagesAndProducts, fi.metatavu.plastep.productinformation.model.ProductSubWorkStage>() {

    @Inject
    lateinit var workStagesTranslator: LemonWorkStagesTranslator

    override fun translate(entity: WorkStagesAndProducts): fi.metatavu.plastep.productinformation.model.ProductSubWorkStage {
        return fi.metatavu.plastep.productinformation.model.ProductSubWorkStage(
            id = entity.workStage.id,
            machineCode = entity.workStage.machineCode,
            machineId = entity.workStage.machineId,
            productCode = entity.product?.sku ?: "",
            productName = entity.product?.name ?: "",
            state = workStagesTranslator.translateWorkStageState(entity.workStage.state),
            startTime = if (!entity.workStage.startTime.isNullOrEmpty()) OffsetDateTime.of(LocalDateTime.parse(entity.workStage.startTime), ZoneOffset.UTC) else null,
            endTime = if (!entity.workStage.endTime.isNullOrEmpty()) OffsetDateTime.of(LocalDateTime.parse(entity.workStage.endTime), ZoneOffset.UTC) else null,
            workAmount = entity.workStage.workAmount?.toFloat(),
            workAmountDone = entity.workStage.workAmountDone?.toFloat()
        )
    }
}

/**
 * Temporary storage of work stage and corresponding product
 */
data class WorkStagesAndProducts(
    val workStage: SubWorkStage,
    val product: Product?
)