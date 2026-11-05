package fi.metatavu.plastep.productinformation.workstages

import fi.metatavu.plastep.productinformation.lemon.LemonWorkStagesController
import fi.metatavu.plastep.productinformation.model.WorkStageState
import fi.metatavu.plastep.productinformation.rest.AbstractApi
import fi.metatavu.plastep.productinformation.spec.WorkStagesApi
import java.time.LocalDate
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.core.Response

@RequestScoped
@Suppress ("UNUSED")
@Consumes ("application/json")
class WorkStagesApiImpl: WorkStagesApi, AbstractApi() {

    @Inject
    lateinit var lemonWorkStagesController: LemonWorkStagesController

    @Inject
    lateinit var lemonWorkStageFindTranslator: LemonWorkStageFindTranslator

    @Inject
    lateinit var lemonWorkStageListTranslator: LemonWorkStageListTranslator

    override fun findWorkStage(workStageId: Long): Response {
        val workStage = lemonWorkStagesController.findWorkStage(workStageId) ?: return createNotFound("Work stage $workStageId not found")
        return createOk(lemonWorkStageFindTranslator.translate(workStage))
    }

    override fun listWorkStages(
        updatedAfter: String,
        updatedBefore: String,
        page: Int?,
        pageSize: Int?,
        state: WorkStageState?
    ): Response {
        val parsedUpdatedAfter: LocalDate?
        val parsedUpdatedBefore: LocalDate?

        try {
            parsedUpdatedAfter = LocalDate.parse(updatedAfter)
            parsedUpdatedBefore = LocalDate.parse(updatedBefore)
        } catch (e: Exception) {
            return createBadRequest("Invalid date format for updatedAfter: $updatedAfter or updatedBefore: $updatedBefore")
        }

        val workStages = lemonWorkStagesController.listWorkStages(
            updatedAfter = parsedUpdatedAfter,
            updatedBefore = parsedUpdatedBefore,
            page = page ?: 1,
            pageSize = pageSize ?: 100
        )

        return createOk(lemonWorkStageListTranslator.translate(workStages))
    }
}