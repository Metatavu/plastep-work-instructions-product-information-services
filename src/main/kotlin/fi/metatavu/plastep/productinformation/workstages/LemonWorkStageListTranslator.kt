package fi.metatavu.plastep.productinformation.workstages

import fi.metatavu.plastep.lemon.client.models.MainWorkStage
import fi.metatavu.plastep.lemon.client.models.WorkStageListResponse
import fi.metatavu.plastep.productinformation.model.WorkStagesListResponse
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Translates the Lemon API WorkStageListResponse to the integration WorkStagesListResponse
 */
@ApplicationScoped
class LemonWorkStageListTranslator: AbstractTranslator<Pair<WorkStageListResponse?, List<MainWorkStage>>, WorkStagesListResponse>() {
    @Inject
    lateinit var lemonWorkStageTranslator: LemonWorkStagesTranslator

    override fun translate(entity: Pair<WorkStageListResponse?, List<MainWorkStage>>): WorkStagesListResponse {
        return WorkStagesListResponse(
            workStages = entity.second.map { lemonWorkStageTranslator.translate(it) },
            hasErrors = entity.first?.hasErrors ?: true,
            hasNextPage = entity.first?.hasNextPage ?: false,
        )
    }

}
