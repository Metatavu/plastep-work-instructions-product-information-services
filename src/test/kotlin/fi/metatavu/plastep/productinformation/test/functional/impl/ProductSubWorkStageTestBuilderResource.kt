package fi.metatavu.plastep.productinformation.test.functional.impl

import org.junit.jupiter.api.fail
import fi.metatavu.jaxrs.test.functional.builder.auth.AccessTokenProvider
import fi.metatavu.plastep.productinformation.client.apis.ProductSubWorkStagesApi
import fi.metatavu.plastep.productinformation.client.infrastructure.ApiClient
import fi.metatavu.plastep.productinformation.client.infrastructure.ClientException
import fi.metatavu.plastep.productinformation.client.models.ProductSubWorkStage
import fi.metatavu.plastep.productinformation.client.models.WorkStageState
import fi.metatavu.plastep.productinformation.test.functional.TestBuilder
import fi.metatavu.plastep.productinformation.test.functional.settings.ApiTestSettings
import org.junit.jupiter.api.Assertions

/**
 * Test builder resource for product sub work stages api
 */
class ProductSubWorkStageTestBuilderResource(
    testBuilder: TestBuilder,
    private val accessTokenProvider: AccessTokenProvider?,
    apiClient: ApiClient
) : ApiTestBuilderResource<ProductSubWorkStage, ApiClient?>(testBuilder, apiClient) {
    override fun clean(t: ProductSubWorkStage?) {
        // read-only
    }

    override fun getApi(): ProductSubWorkStagesApi {
        ApiClient.accessToken = accessTokenProvider?.accessToken
        return ProductSubWorkStagesApi(ApiTestSettings.apiBasePath)
    }

    /**
     * Lists sub work stages
     *
     * @param machineCode machine code
     * @param state state
     * @return work stages with product info
     */
    fun list(
        machineCode: String? = null,
        state: WorkStageState? = null
    ): Array<ProductSubWorkStage> {
        return api.listSubWorkStages(
            machineCode = machineCode,
            state = state
        )
    }

    /**
     * Asserts that list fails
     *
     * @param expectedStatus expected fail status
     * @param machineCode machine code
     * @param state state
     */
    fun assertListFailStatus(
        expectedStatus: Int,
        machineCode: String?,
        state: WorkStageState?
    ) {
        try {
            api.listSubWorkStages(
                machineCode = machineCode,
                state = state
            )
            fail("Expected list to fail")
        } catch (e: ClientException) {
            Assertions.assertEquals(expectedStatus, e.statusCode)
        }
    }

}