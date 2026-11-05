package fi.metatavu.plastep.productinformation.test.functional.resources

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.tomakehurst.wiremock.client.WireMock.*
import fi.metatavu.plastep.lemon.client.models.*
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer

/**
 * Resource for Lemon mock service container
 */
class LemonRestResource : QuarkusTestResourceLifecycleManager {

    override fun start(): Map<String, String> {
        container.start()

        val host = container.host
        val port = container.getMappedPort(8080)

        configureFor(host, port)

        createLoginStubs()
        createProductStubs()
        createMachineStubs()
        createMainWorkStageStubs()
        createAvailableWorkStageStubs()

        return mapOf(
            "lemon.rest.url" to "http://$host:$port",
            "lemon.rest.database" to DATABASE,
            "lemon.rest.apikey" to APIKEY,
            "lemon.rest.username" to USERNAME,
            "lemon.rest.password" to PASSWORD
        )
    }

    /**
     * Create stubs for Lemon login endpoints
     */
    private fun createLoginStubs() {
        val objectMapper = jacksonObjectMapper()

        stubFor(
            post(urlPathEqualTo("/api/auth/login"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(
                    LoginPayload(
                        apiKey = APIKEY,
                        database = DATABASE,
                        userName = USERNAME,
                        password = PASSWORD
                    )
                )))
                .willReturn(jsonResponse(objectMapper.writeValueAsString(
                    LoginResult(
                        sessionId = SESSION_ID,
                        code = 200,
                        message = "ok",
                        version = "1.2.3"
                    )
                ), 200))
        )
    }

    /**
     * Machine listing endpoints stubs
     */
    private fun createMachineStubs() {
        val objectMapper = jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        val machines = (1..4)
            .map { "/lemon/test-machine-${it}.json" }
            .map { objectMapper.readValue<Machine>(this.javaClass.getResource(it)!!) }

        stubFor(
            get(urlPathEqualTo("/api/production/machines"))
                .withQueryParams(
                    mapOf(
                        "filter.page" to equalTo("1"),
                        "filter.page_size" to equalTo("50")
                    )
                )
                .willReturn(
                    jsonResponse(
                        objectMapper.writeValueAsString(
                            MachineListResult(
                                results = machines.toTypedArray(),
                                hasNextPage = false,
                                hasErrors = false
                            )
                        ), 200
                    )
                )
        )

        stubFor(
            get(urlPathEqualTo("/api/production/machines"))
                .withQueryParams(
                    mapOf(
                        "filter.page" to equalTo("1"),
                        "filter.page_size" to equalTo("2")
                    )
                )
                .willReturn(
                    jsonResponse(
                        objectMapper.writeValueAsString(
                            MachineListResult(
                                results = machines.subList(1, 3).toTypedArray(),
                                hasNextPage = true,
                                hasErrors = false
                            )
                        ), 200
                    )
                )
        )

        stubFor(
            get(urlPathEqualTo("/api/production/machines"))
                .withQueryParams(
                    mapOf(
                        "filter.page" to equalTo("2"),
                        "filter.page_size" to equalTo("2")
                    )
                )
                .willReturn(
                    jsonResponse(
                        objectMapper.writeValueAsString(
                            MachineListResult(
                                results = machines.subList(2, 4).toTypedArray(),
                                hasNextPage = false,
                                hasErrors = false
                            )
                        ), 200
                    )
                )
        )

        stubFor(
            get(urlPathEqualTo("/api/production/machines"))
                .withQueryParams(
                    mapOf(
                        "filter.page_size" to equalTo("0")
                    )
                )
                .willReturn(
                    jsonResponse(
                        objectMapper.writeValueAsString(
                            MachineListResult(
                                results = arrayOf(),
                                hasNextPage = false,
                                hasErrors = false
                            )
                        ), 200
                    )
                )
        )
    }

    /**
     * Creates stubs for Lemon product endpoints
     */
    private fun createProductStubs() {
        val objectMapper = jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        val products = (1..4)
            .map { "/lemon/test-product-${it}.json" }
            .map { objectMapper.readValue<Product>(this.javaClass.getResource(it)!!) }

        val productStructures = (1..4)
            .map { "/lemon/test-product-${it}-structure.json" }
            .map { objectMapper.readValue<GetProductStructureResult>(this.javaClass.getResource(it)!!) }

        products.forEachIndexed { index, product ->
            stubFor(
                get(urlPathEqualTo("/api/products/${product.id}"))
                    .willReturn(jsonResponse(objectMapper.writeValueAsString(GetProductResult(
                        result = product,
                        hasErrors = false,
                        ok = true
                    )), 200))
            )

            stubFor(
                get(urlPathEqualTo("/api/products/${product.sku}/0/1"))
                    .willReturn(jsonResponse(objectMapper.writeValueAsString(productStructures[index]), 200))
            )
        }

        stubFor(
            get(urlPathEqualTo("/api/products"))
                .withQueryParams(mapOf(
                    "filter.page" to equalTo("1"),
                    "filter.page_size" to equalTo("10")
                ))
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ProductListResult(
                    results = products.toTypedArray(),
                    hasNextPage = false,
                    hasErrors = false
                )), 200))
        )

        stubFor(
            get(urlPathEqualTo("/api/products"))
                .withQueryParams(mapOf(
                    "filter.page" to equalTo("1"),
                    "filter.page_size" to equalTo("2")
                ))
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ProductListResult(
                    results = products.subList(1, 3).toTypedArray(),
                    hasNextPage = true,
                    hasErrors = false
                )), 200))
        )

        stubFor(
            get(urlPathEqualTo("/api/products"))
                .withQueryParams(mapOf(
                    "filter.page" to equalTo("2"),
                    "filter.page_size" to equalTo("2")
                ))
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ProductListResult(
                    results = products.subList(2, 4).toTypedArray(),
                    hasNextPage = false,
                    hasErrors = false
                )), 200))
        )

        stubFor(
            get(urlPathEqualTo("/api/products"))
                .withQueryParams(mapOf(
                    "filter.page_size" to equalTo("0")
                ))
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ProductListResult(
                    results = arrayOf(),
                    hasNextPage = false,
                    hasErrors = false
                )), 200))
        )

        // List products by sku filter
        stubFor(
            get(urlPathEqualTo("/api/products"))
                .withQueryParams(mapOf(
                    "filter.page" to equalTo("1"),
                    "filter.page_size" to equalTo("1"),
                    "filter.sku" to equalTo("000223")
                ))
                .willReturn(jsonResponse(objectMapper.writeValueAsString(ProductListResult(
                    results = products.filter { it.sku == "000223" }.toTypedArray(),
                    hasNextPage = false,
                    hasErrors = false
                )), 200))
        )
    }

    /**
     * Work stage listing endpoints stubs
     */
    private fun createMainWorkStageStubs() {
        val objectMapper = jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        val mainWorkStages = (1..3)
            .map { "/lemon/test-main-work-stage-${it}.json" }
            .map { objectMapper.readValue<WorkStageListItem>(this.javaClass.getResource(it)!!) }

        val mainWorkStagesWithSubWorkStages = (1..3)
            .map { "/lemon/test-main-work-stage-with-sub-work-stages-${it}.json" }
            .map { objectMapper.readValue<MainWorkStage>(this.javaClass.getResource(it)!!) }

        stubFor(
            get(urlPathEqualTo("/api/production/main_ws/changed"))
                .withQueryParams(
                    mapOf(
                        "filter.updated_after" to equalTo("1971-01-01"),
                        "filter.updated_before" to equalTo("2030-01-01"),
                        "filter.page" to equalTo("1"),
                        "filter.page_size" to equalTo("50")
                    )
                )
                .willReturn(
                    jsonResponse(
                        objectMapper.writeValueAsString(
                            WorkStageListResponse(
                                results = mainWorkStages.toTypedArray(),
                                hasErrors = false,
                                hasNextPage = false
                            )
                        ), 200
                    )
                )
        )

        mainWorkStagesWithSubWorkStages.forEach { mainWorkStage ->
            stubFor(
                get(urlPathEqualTo("/api/production/main_ws/${mainWorkStage.id}"))
                    .willReturn(
                        jsonResponse(
                            objectMapper.writeValueAsString(
                                MainWorkStageResponse(
                                    result = mainWorkStage,
                                    ok = true,
                                    hasErrors = false
                                )
                            ), 200
                        )
                    )
            )
        }
    }

    /**
     * Available sub work stages stubs
     */
    private fun createAvailableWorkStageStubs() {
        val objectMapper = jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        val subWorkStage = objectMapper.readValue<SubWorkStage>(this.javaClass.getResource("/lemon/test-sub-work-stage-1.json")!!)

        stubFor(
            get(urlPathEqualTo("/api/production/main_ws/sub_ws/available"))
                .willReturn(
                    jsonResponse(
                        objectMapper.writeValueAsString(
                            SubWorkStageListResponse(
                                results = arrayOf(subWorkStage),
                                hasErrors = false
                            )
                        ), 200
                    )
                )
        )
    }

    override fun stop() {
        container.stop()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LemonRestResource::class.java)

        var container: GenericContainer<*> = GenericContainer("wiremock/wiremock:3.13.2")
            .withLogConsumer {
                logger.info(it.utf8String)
            }
            .withExposedPorts(8080)

        const val DATABASE = "test"
        const val APIKEY = "testkey"
        const val USERNAME = "testuser"
        const val PASSWORD = "testpass"
        const val SESSION_ID = "test-session-id"
    }

}