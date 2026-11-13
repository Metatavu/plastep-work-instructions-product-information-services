package fi.metatavu.plastep.productinformation.machines

import fi.metatavu.plastep.lemon.client.models.MachineListResult
import fi.metatavu.plastep.productinformation.model.MachinesListResponse
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Translates Lemon machine list response to REST machine list response
 */
@ApplicationScoped
class LemonMachineListTranslator : AbstractTranslator<MachineListResult?, MachinesListResponse>() {

    @Inject
    lateinit var lemonMachineTranslator: LemonMachineTranslator

    override fun translate(entity: MachineListResult?): MachinesListResponse {
        return MachinesListResponse(
            machines = entity?.results?.map(lemonMachineTranslator::translate),
            hasErrors = entity?.hasErrors ?: true,
            hasNextPage = entity?.hasNextPage ?: false
        )
    }

}
