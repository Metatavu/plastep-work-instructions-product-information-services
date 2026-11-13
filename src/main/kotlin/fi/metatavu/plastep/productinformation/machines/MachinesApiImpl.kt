package fi.metatavu.plastep.productinformation.machines

import fi.metatavu.plastep.productinformation.lemon.LemonMachinesController
import fi.metatavu.plastep.productinformation.rest.AbstractApi
import fi.metatavu.plastep.productinformation.spec.MachinesApi
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response

@RequestScoped
class MachinesApiImpl : MachinesApi, AbstractApi() {

    @Inject
    lateinit var lemonMachinesController: LemonMachinesController

    @Inject
    lateinit var lemonMachineListTranslator: LemonMachineListTranslator

    override fun listMachines(page: Int?, pageSize: Int?): Response {
        if (!hasIntegrationRole()) {
            return createForbidden("Only integration users can access this resource")
        }

        val lemonMachines = lemonMachinesController.listMachines(
            page ?: 1,
            pageSize ?: 50
        )

        return createOk(lemonMachineListTranslator.translate(lemonMachines))
    }
}