package fi.metatavu.plastep.productinformation.workstages

import fi.metatavu.plastep.productinformation.model.MainWorkStage
import fi.metatavu.plastep.productinformation.model.SubWorkStage
import fi.metatavu.plastep.productinformation.model.WorkStageState
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped

/**
 * Translates Lemon work stage to REST WorkStage
 *
 * @author Jari Nykänen
 */
@ApplicationScoped
class LemonWorkStagesTranslator : AbstractTranslator<fi.metatavu.plastep.lemon.client.models.MainWorkStage, MainWorkStage>() {

    override fun translate(entity: fi.metatavu.plastep.lemon.client.models.MainWorkStage): MainWorkStage {
        return MainWorkStage(
            id = entity.id,
            state = translateWorkStageState(entity.state),
            productCode = entity.productCode,
            comment = entity.comment,
            workNumber = entity.worknumber,
            subWorkStages =  entity.subWorkstages?.map { translateSubWorkStage(it) } ?: emptyList(),
        )
    }

    fun translateWorkStageState(lemonState: Int): WorkStageState {
        return when (lemonState) {
            2 -> WorkStageState.ACCEPTED
            3 -> WorkStageState.IN_PROGRESS
            4 -> WorkStageState.WAITING
            5 -> WorkStageState.INTERRUPTED
            9 -> WorkStageState.COMPLETED
            else -> WorkStageState.UNKNOWN
        }
    }

    private fun translateSubWorkStage(lemonSubWorkStage: fi.metatavu.plastep.lemon.client.models.SubWorkStage): SubWorkStage {
        return SubWorkStage(
            id = lemonSubWorkStage.id,
            workPhaseId = lemonSubWorkStage.workphaseId,
            state = translateWorkStageState(lemonSubWorkStage.state),
            machineId = lemonSubWorkStage.machineId,
            machineCode = lemonSubWorkStage.machineCode,
            description = lemonSubWorkStage.description,
            description2 = lemonSubWorkStage.description2,
            productCode = lemonSubWorkStage.productCode
        )
    }

}
