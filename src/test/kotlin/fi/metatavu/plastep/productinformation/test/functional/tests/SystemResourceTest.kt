package fi.metatavu.plastep.productinformation.test.functional.tests

import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.containsStringIgnoringCase
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test

/**
 * Tests for system resources
 *
 * @author Antti Leppä
 */
@QuarkusTest
class SystemResourceTest : AbstractResourceTest() {

    @Test
    fun testPingEndpoint() {
        given()
            .contentType("application/json")
            .`when`().get("/v1/system/ping")
            .then()
            .statusCode(200)
            .body(`is`("pong"))
    }

    @Test
    fun testCorsPreflightFromOtherOrigin() {
        given()
            .header("Origin", "https://management.example.com")
            .header("Access-Control-Request-Method", "GET")
            .header("Access-Control-Request-Headers", "authorization")
            .`when`().options("/v1/system/ping")
            .then()
            .statusCode(200)
            .header("Access-Control-Allow-Origin", "https://management.example.com")
            .header("Access-Control-Allow-Headers", containsStringIgnoringCase("authorization"))
    }

}