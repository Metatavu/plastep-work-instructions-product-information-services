package fi.metatavu.plastep.productinformation.workstages

import fi.metatavu.plastep.lemon.client.models.MainWorkStageResponse
import fi.metatavu.plastep.productinformation.model.WorkStageFindResponse
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Translates Lemonsoft response for finding work stage to integrations service rest response
 */
@ApplicationScoped
class LemonWorkStageFindTranslator: AbstractTranslator<MainWorkStageResponse, WorkStageFindResponse>() {

    @Inject
    lateinit var lemonWorkStageTranslator: LemonWorkStagesTranslator

    override fun translate(entity: MainWorkStageResponse): WorkStageFindResponse {
        return WorkStageFindResponse(
            workStage = entity.result?.let(lemonWorkStageTranslator::translate),
            hasErrors = entity.hasErrors,
            ok = entity.ok
        )
    }
}