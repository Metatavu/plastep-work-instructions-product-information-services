package fi.metatavu.plastep.productinformation.rest

import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext

/**
 * Abstract base class for all API services
 *
 * @author Antti Leppä
 */
@RequestScoped
abstract class AbstractApi {

    @Context
    lateinit var securityContext: SecurityContext

    /**
     * Checks if user has integration role
     *
     * @return if user has integration role
     */
    protected fun hasIntegrationRole(): Boolean {
        return securityContext.isUserInRole("integration")
    }

    /**
     * Checks if user has admin role
     *
     * @return if user has admin role
     */
    protected fun hasAdminRole(): Boolean {
        return securityContext.isUserInRole("admin")
    }

    /**
     * Checks if user has worker role
     *
     * @return if user has worker role
     */
    protected fun hasWorkerRole(): Boolean {
        return securityContext.isUserInRole("worker")
    }


    /**
     * Constructs ok response
     *
     * @param entity payload
     * @return response
     */
    protected fun createOk(entity: Any?): Response {
        return Response
            .status(Response.Status.OK)
            .entity(entity)
            .build()
    }

    /**
     * Constructs not found response
     *
     * @param message message
     * @return response
     */
    protected fun createNotFound(message: String): Response {
        return createError(Response.Status.NOT_FOUND, message)
    }

    /**
     * Constructs forbidden response
     *
     * @param message message
     * @return response
     */
    protected fun createForbidden(message: String): Response {
        return createError(Response.Status.FORBIDDEN, message)
    }

    /**
     * Constructs bad request response
     *
     * @param message message
     * @return response
     */
    protected fun createBadRequest(message: String): Response {
        return createError(Response.Status.BAD_REQUEST, message)
    }

    /**
     * Creates not found response with given parameters
     *
     * @param target target of the find method
     * @param id ID of the target
     */
    protected fun createNotFoundWithMessage(target: String, id: Int): Response {
        return createNotFound("$target with ID $id could not be found")
    }

    /**
     * Constructs an error response
     *
     * @param status status code
     * @param message message
     *
     * @return error response
     */
    private fun createError(status: Response.Status, message: String): Response {
        val entity =  fi.metatavu.plastep.productinformation.model.Error(
            message = message,
            code = status.statusCode
        )

        return Response
            .status(status)
            .entity(entity)
            .build()
    }

    companion object {
        const val PRODUCT = "Product"
        const val WORK_STAGE = "Work stage"
    }

}
