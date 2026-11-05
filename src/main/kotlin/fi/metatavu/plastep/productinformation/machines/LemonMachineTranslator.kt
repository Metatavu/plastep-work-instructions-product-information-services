package fi.metatavu.plastep.productinformation.machines

import fi.metatavu.plastep.lemon.client.models.Machine
import fi.metatavu.plastep.productinformation.rest.AbstractTranslator
import jakarta.enterprise.context.ApplicationScoped

/**
 * Translates Lemonsoft REST machine to simplified REST object
 */
@ApplicationScoped
class LemonMachineTranslator : AbstractTranslator<Machine, fi.metatavu.plastep.productinformation.model.Machine>() {
    override fun translate(entity: Machine): fi.metatavu.plastep.productinformation.model.Machine {
        return fi.metatavu.plastep.productinformation.model.Machine(
            id = entity.id,
            code = entity.code,
            description = entity.description
        )
    }
}
