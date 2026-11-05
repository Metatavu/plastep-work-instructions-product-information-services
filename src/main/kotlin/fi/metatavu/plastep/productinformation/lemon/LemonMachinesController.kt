package fi.metatavu.plastep.productinformation.lemon

import fi.metatavu.plastep.lemon.client.models.MachineListResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Controller for Lemonsoft machines
 */
@ApplicationScoped
class LemonMachinesController {

    @Inject
    lateinit var lemonClient: LemonClient

    /**
     * List machines from Lemonsoft
     *
     * @param page page number.
     * @param pageSize page size.
     * @return lemon machine list response
     */
    fun listMachines(
        page: Int,
        pageSize: Int
    ): MachineListResult? {
        return lemonClient.listMachines(
            filterPage = page,
            filterPageSize = pageSize,
        )
    }

}