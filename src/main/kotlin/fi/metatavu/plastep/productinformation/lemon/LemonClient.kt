package fi.metatavu.plastep.productinformation.lemon

import fi.metatavu.plastep.lemon.client.apis.*
import fi.metatavu.plastep.lemon.client.infrastructure.ApiClient
import fi.metatavu.plastep.lemon.client.models.*
import okhttp3.OkHttpClient
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.RequestScoped
import jakarta.inject.Inject

/**
 * Client for Lemon REST API
 */
@RequestScoped
class LemonClient {

    @ConfigProperty(name = "lemon.rest.url")
    lateinit var lemonRestUrl: String

    @ConfigProperty(name = "lemon.rest.database")
    lateinit var lemonRestDatabase: String

    @ConfigProperty(name = "lemon.rest.apikey")
    lateinit var lemonRestApiKey: String

    @ConfigProperty(name = "lemon.rest.username")
    lateinit var lemonRestUsername: String

    @ConfigProperty(name = "lemon.rest.password")
    lateinit var lemonRestPassword: String

    private var sessionId: String? = null

    /**
     * Post construct
     */
    @PostConstruct
    fun init() {
        sessionId = null
    }

    @Inject
    lateinit var logger: Logger

    /**
     * Returns single product from Lemonsoft REST API
     *
     * @param productId product id
     * @return product search result lemon object
     */
    fun findProduct(productId: Int): GetProductResult? {
        val product = try {
            getProductsApi().getProduct(id = productId)
        } catch (ex: Exception) {
            logger.error("Failed to find product with ID $productId.")
            logger.error("Error from Lemonsoft: ${ex.message}")
            return null
        }
        if (product.hasErrors || !product.errors.isNullOrEmpty()) {
            logger.error("Failed to find product with ID $productId.")
            logger.error("Error from Lemonsoft ${getErrorsString(product.errors)}")
            return null
        }
        return product
    }

    /**
     * Lists products from Lemonsoft
     *
     * @param filterName filter by name
     * @param filterSku filter by SKU
     * @param filterWithImages  filter with images
     * @param filterModifiedBefore  filter by modified before
     * @param filterModifiedAfter  filter by modified after
     * @param filterAttributeId  filter by attribute id
     * @param filterExtraName  filter by extra name
     * @param filterCategoryId  filter by category id
     * @param filterGroupCode  filter by group code
     * @param filterShowModels filter by show models
     * @param filterShowNonactive filter by show nonactive
     * @param filterShelf filter by shelf
     * @param filterShelfStock filter by shelf stock
     * @param filterObjectIds filter by object ids
     * @param filterPage Page number. If not provided, using default value of 1 (optional)
     * @param filterPageSize Page size. If not provided, using default value of 10 (optional)
     * @param filterSearch filter by search
     * @return lemon product list object
     */
    fun listProducts(
        filterName: String? = null,
        filterSku: Array<String>? = null,
        filterWithImages: Boolean? = null,
        filterModifiedBefore: String? = null,
        filterModifiedAfter: String? = null,
        filterAttributeId: Int? = null,
        filterExtraName: String? = null,
        filterCategoryId: Int? = null,
        filterGroupCode: Int? = null,
        filterShowModels: Boolean? = null,
        filterShowNonactive: Boolean? = null,
        filterShelf: String? = null,
        filterShelfStock: Int? = null,
        filterObjectIds: Array<Int>? = null,
        filterPage: Int? = null,
        filterPageSize: Int? = null,
        filterSearch: String? = null
    ): ProductListResult? {
        val response = try {
            getProductsApi().listProducts(
                filterName = filterName,
                filterSku = filterSku,
                filterWithImages = filterWithImages,
                filterModifiedBefore = filterModifiedBefore,
                filterModifiedAfter = filterModifiedAfter,
                filterAttributeId = filterAttributeId,
                filterExtraName = filterExtraName,
                filterCategoryId = filterCategoryId,
                filterGroupCode = filterGroupCode,
                filterShowModels = filterShowModels,
                filterShowNonactive = filterShowNonactive,
                filterShelf = filterShelf,
                filterShelfStock = filterShelfStock,
                filterObjectIds = filterObjectIds,
                filterPage = filterPage,
                filterPageSize = filterPageSize,
                filterSearch = filterSearch
            )
        } catch (ex: Exception) {
            logger.error("Failed to list products from Lemonsoft.")
            logger.error("Error message from Lemonsoft: ${ex.message}")
            return null
        }
        if (response.hasErrors || !response.errors.isNullOrEmpty()) {
            logger.error("Failed to list products from Lemonsoft.")
            logger.error("Error message from Lemonsoft: ${getErrorsString(response.errors)}")
            return null
        }
        return response
    }

    /**
     * Returns product structure from Lemonsoft REST API
     *
     * @param productCode product code
     * @param workNumber Work number. Use 0 for default structure
     * @param level Structure level to be fetched
     * @return product structure response lemon object
     */
    fun getProductStructure(productCode: String, workNumber: Int, level: Int): GetProductStructureResult? {
        val response = try {
            getProductsApi().getProductStructure(
                productCode = productCode,
                workNumber = workNumber,
                level = level
            )

        } catch (ex: Exception) {
            logger.error("Failed to get product structure for product code: $productCode, work number: $workNumber and level: $level", ex)
            return null
        }
        if (response.hasErrors == true || !response.errors.isNullOrEmpty()) {
            logger.error("Failed to get product structure for product code: $productCode, work number: $workNumber and level: $level")
            logger.error("Error message from Lemonsoft: ${getErrorsString(response.errors)}")
            return null
        }

        return response
    }

    /**
     * Returns machines from Lemonsoft REST API
     *
     * @param filterCode filter by code
     * @param filterType filter by type
     * @param filterIsDisabled filter by is disabled
     * @param filterObjectIds filter by object ids
     * @param filterPage Page number.
     * @param filterPageSize Page size.
     * @param filterSearch filter by search
     * @return lemon machine list response
     */
    fun listMachines(
        filterCode: String? = null,
        filterType: Int? = null,
        filterIsDisabled: Boolean? = null,
        filterObjectIds: Array<Int>? = null,
        filterPage: Int? = null,
        filterPageSize: Int? = null,
        filterSearch: String? = null
    ): MachineListResult? {
        val response = try {
            getMachinesApi().listMachines(
                filterCode = filterCode,
                filterType = filterType,
                filterIsDisabled = filterIsDisabled,
                filterObjectIds = filterObjectIds,
                filterPage = filterPage,
                filterPageSize = filterPageSize,
                filterSearch = filterSearch
            )
        } catch (ex: Exception) {
            logger.error("Failed to list machines")
            logger.error("Error message from Lemonsoft: ${ex.message}")
            return null
        }
        if (response.hasErrors || !response.errors.isNullOrEmpty()) {
            logger.error("Failed to list machines")
            logger.error("Error message from Lemonsoft: ${getErrorsString(response.errors)}")
            return null
        }
        return response
    }

    /**
     * Returns single main work stage from Lemonsoft REST API
     *
     * @param workStageId work stage id
     * @return lemon work starge find response
     */
    fun findWorkStage(workStageId: Long): MainWorkStageResponse? {
        val workStage = try {
            getWorkStagesApi().findWorkStage(id = workStageId)
        } catch (ex: Exception) {
            logger.error("Failed to find work stage with ID: $workStageId.")
            logger.error("Error message from Lemonsoft: ${ex.message}")
            return null
        }
        if (workStage.hasErrors || !workStage.errors.isNullOrEmpty()) {
            logger.error("Failed to find work stage with ID: $workStageId.")
            logger.error("Error message from Lemonsoft: ${getErrorsString(workStage.errors)}")
            return null
        }
        return workStage
    }

    /**
     * Lists work stages from Lemonsoft
     *
     * @param filterUpdatedAfter filter by updated after
     * @param filterUpdatedBefore filter by updated before
     * @param filterPage Page number. If not provided, using default value of 1 (optional)
     * @param filterPageSize Page size. If not provided, using default value of 10 (optional)
     * @param filterState filter by state
     * @return work stage list response
     */
    fun listWorkStages(
        filterUpdatedAfter: LocalDate,
        filterUpdatedBefore: LocalDate,
        filterPage: Int,
        filterPageSize: Int,
        filterState: Int? = null
    ): WorkStageListResponse? {
        val response = try {
            getWorkStagesApi().listWorkStages(
                filterUpdatedAfter = filterUpdatedAfter.toString(),
                filterUpdatedBefore = filterUpdatedBefore.toString(),
                filterPage = filterPage,
                filterPageSize = filterPageSize,
                filterState = filterState
            )
        } catch (ex: Exception) {
            logger.error("Failed to list work stages with filters: updated after: $filterUpdatedAfter, updated before: $filterUpdatedBefore, page: $filterPage, page size: $filterPageSize, state: $filterState")
            logger.error("Error message from Lemonsoft: ${ex.message}")
            return null
        }
        if (response.hasErrors || !response.errors.isNullOrEmpty()) {
            logger.error("Failed to list work stages with filters: updated after: $filterUpdatedAfter, updated before: $filterUpdatedBefore, page: $filterPage, page size: $filterPageSize, state: $filterState")
            logger.error("Error message from Lemonsoft: ${getErrorsString(response.errors)}")
            return null
        }
        return response
    }

    /**
     * Lists sub work stages
     *
     * @param machineCode optional machine code
     * @return response
     */
    fun listSubWorkStages(
        machineCode: String?
    ): SubWorkStageListResponse? {
        val response = try {
            getSubWorkStageApi().listAvailableSubWorkStages(
                machineCode = machineCode
            )
        } catch (ex: Exception) {
            logger.error("Failed to list sub work stages with filters: machine code: $machineCode")
            logger.error("Error message from Lemonsoft: ${ex.message}")
            return null
        }
        if (response.hasErrors || !response.errors.isNullOrEmpty()) {
            logger.error("Failed to list sub work stages with filters: machine code: $machineCode")
            logger.error("Error message from Lemonsoft: ${getErrorsString(response.errors)}")
            return null
        }
        return response
    }

    /**
     * Returns initialized product API
     *
     * @return product API
     */
    private fun getProductsApi(): ProductApi {
        ensureSession()

        return ProductApi(
            basePath = lemonRestUrl,
            client = getClient()
        )
    }

    /**
     * Returns initialized sub work stages api
     */
    private fun getSubWorkStageApi(): SubWorkStagesApi {
        ensureSession()

        return SubWorkStagesApi(
            basePath = lemonRestUrl,
            client = getClient()
        )
    }

    /**
     * Returns initialized machine API
     *
     * @return machine API
     */
    private fun getMachinesApi(): MachineApi {
        ensureSession()

        return MachineApi(
            basePath = lemonRestUrl,
            client = getClient()
        )
    }

    /**
     * Returns initialized work stage API
     *
     * @return work stage API
     */
    private fun getWorkStagesApi(): WorkStagesApi {
        ensureSession()

        return WorkStagesApi(
            basePath = lemonRestUrl,
            client = getClient()
        )
    }

    /**
     * Ensures that session is active
     */
    private fun ensureSession() {
        if (sessionId == null) {
            login()
        }

        ApiClient.apiKey["Session-Id"] = sessionId ?: throw LemonException("Session id is null")
    }

    /**
     * Logs in to Lemonsoft REST API
     */
    private fun login() {
        val authenticationApi = AuthenticationApi(
            basePath = lemonRestUrl,
            client = getClient()
        )

        val result = authenticationApi.login(
            LoginPayload(
                database = lemonRestDatabase,
                apiKey = lemonRestApiKey,
                userName = lemonRestUsername,
                password = lemonRestPassword
            )
        )

        sessionId = result.sessionId
    }

    /**
     * Unites all the errors from lemonsoft to string
     *
     * @param errors errors
     * @return string of errors
     */
    private fun getErrorsString(errors: Array<Error>?): String {
        return errors?.joinToString(", ") { "${it.code} ${it.message}" } ?: ""
    }

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .build()
    }
}