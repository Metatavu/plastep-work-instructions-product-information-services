package fi.metatavu.plastep.productinformation.lemon

import fi.metatavu.plastep.lemon.client.models.MainWorkStage
import fi.metatavu.plastep.lemon.client.models.MainWorkStageResponse
import fi.metatavu.plastep.lemon.client.models.SubWorkStage
import fi.metatavu.plastep.lemon.client.models.WorkStageListResponse
import fi.metatavu.plastep.productinformation.model.WorkStageState
import fi.metatavu.plastep.productinformation.workstages.LemonWorkStagesTranslator
import org.slf4j.Logger
import java.time.LocalDate
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Controller for Lemonsoft workStages
 */
@ApplicationScoped
class LemonWorkStagesController {

    @Inject
    lateinit var lemonClient: LemonClient

    @Inject
    lateinit var logger: Logger

    /**
     * Find workStage from Lemonsoft
     *
     * @param workStageId workStage id
     * @return workStage response
     */
    fun findWorkStage(workStageId: Long): MainWorkStageResponse? {
        return lemonClient.findWorkStage(workStageId = workStageId)
    }

    /**
     * Lists workStages from Lemonsoft
     *
     * @param updatedAfter updated after
     * @param updatedBefore updated before
     * @param page page number. Page number starts from 0
     * @param pageSize page size.
     * @return response and list of workStages with filled info
     */
    fun listWorkStages(
        updatedAfter: LocalDate,
        updatedBefore: LocalDate,
        page: Int,
        pageSize: Int
    ): Pair<WorkStageListResponse?, List<MainWorkStage>> {
        val mainWorkStageResponse = lemonClient.listWorkStages(
            filterUpdatedAfter = updatedAfter,
            filterUpdatedBefore = updatedBefore,
            filterPage = page,
            filterPageSize = pageSize
        )

        val mainWorkStageIds = mainWorkStageResponse?.results?.map { it.id } ?: emptyList()

        val withFilledData = mainWorkStageIds.map {
            val foundWorkStage = findWorkStage(it)
            if (foundWorkStage == null || foundWorkStage.hasErrors || !foundWorkStage.ok) {
                logger.error("Failed to find workStage with id {}", it)
            }

            foundWorkStage?.result
        }
        return mainWorkStageResponse to withFilledData.mapNotNull { it }
    }

    /**
     * Lists available work stages from lemonsoft
     *
     * @param state state filter
     * @param machineCode machine code filter
     * @return list of work stages
     */
    fun listSubWorkStages(
        state: WorkStageState?,
        machineCode: String?
    ): List<SubWorkStage> {
        val subWorkStages = lemonClient.listSubWorkStages(
            machineCode = machineCode,
        )

        val filteredByState: List<SubWorkStage?>? = if (state == null) {
            subWorkStages?.results?.toList()
        } else {
            filterByWorkState(state, subWorkStages?.results?.toList())
        }

        return filteredByState?.filterNotNull() ?: emptyList()
    }

    /**
     * Filters lemonsoft sub work stages by state
     *
     * @param lemonState REST object state
     * @param result result
     * @return filtered list
     */
    private fun filterByWorkState(lemonState: WorkStageState, result: List<SubWorkStage>?): List<SubWorkStage> {
        if (result == null) return emptyList()
        return when (lemonState) {
            WorkStageState.ACCEPTED -> result.filter { it.state == 2 }
            WorkStageState.IN_PROGRESS -> result.filter { it.state == 3 }
            WorkStageState.WAITING -> result.filter { it.state == 4 }
            WorkStageState.INTERRUPTED -> result.filter { it.state == 5 }
            WorkStageState.COMPLETED -> result.filter { it.state == 9 }
            WorkStageState.UNKNOWN -> result.filter { it.state != 2 && it.state != 3 && it.state != 4 && it.state != 5 && it.state != 9 }
        }
    }
}