package fi.metatavu.plastep.productinformation.test.functional.tests

import fi.metatavu.plastep.productinformation.client.models.WorkStageState
import fi.metatavu.plastep.productinformation.test.functional.resources.LemonRestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Tests for work stage endpoints
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(LemonRestResource::class)
)
class WorkStagesTestIT: AbstractResourceTest() {

    @Test
    fun testListWorkStages(): Unit = createTestBuilder().use {
        val allWorkStages = it.admin.workStages.list(
            updatedBefore = "2030-01-01",
            updatedAfter = "1971-01-01",
            page = 1,
            pageSize = 50
        ).workStages!!

        assertEquals(3, allWorkStages.size)

        val workStage1 = allWorkStages.find{ it.id == 5470L}

        assertNotNull(workStage1)
        assertEquals(workStage1!!.workNumber, 1013)
        assertEquals(workStage1.productCode, "161480")
        assertEquals(workStage1.state, WorkStageState.COMPLETED)
        assertEquals(workStage1.subWorkStages.size, 3)

        val subWorkStages = workStage1.subWorkStages

        val subWorkStage1 = subWorkStages.find{ it.id == 17713L }
        assertNotNull(subWorkStage1)
        assertEquals(subWorkStage1!!.productCode, "161480")
        assertEquals(subWorkStage1.workPhaseId, 13)
        assertEquals(subWorkStage1.state, WorkStageState.IN_PROGRESS)
        assertEquals(subWorkStage1.description, "Mittaus ja laadunvalvonta")
        assertEquals(subWorkStage1.machineCode, "KP muut")
        assertEquals(subWorkStage1.machineId, 33)

        val subWorkStage2 = subWorkStages.find{ it.id == 17714L }
        assertNotNull(subWorkStage2)
        assertEquals(subWorkStage2!!.productCode, "161480")
        assertEquals(subWorkStage2.workPhaseId, 11)
        assertEquals(subWorkStage2.state, WorkStageState.COMPLETED)
        assertEquals(subWorkStage2.description, "UÄ-hitsaus")
        assertEquals(subWorkStage2.machineCode, "KP muut")
        assertEquals(subWorkStage2.machineId, 33)

        val subWorkStage3 = subWorkStages.find{ it.id == 17715L }
        assertNotNull(subWorkStage3)
        assertEquals(subWorkStage3!!.productCode, "161480")
        assertEquals(subWorkStage3.workPhaseId, 2)
        assertEquals(subWorkStage3.state, WorkStageState.COMPLETED)
        assertEquals(subWorkStage3.description, "Kokoonpano")
        assertEquals(subWorkStage3.machineCode, "KP muut")
        assertEquals(subWorkStage3.machineId, 33)
    }

    @Test
    fun testListProductsInvalidUser() = createTestBuilder().use {
        it.admin.product.assertListFail(expectedStatus = 403)
        it.empty.product.assertListFail(expectedStatus = 401)
        it.notvalid.product.assertListFail(expectedStatus = 401)

        it.admin.workStages.assertListFail(
            400,
            updatedBefore = "should-fail",
            updatedAfter = "1971-01-01",
            page = 1,
            pageSize = 50
        )

        it.admin.workStages.assertListFail(
            400,
            updatedBefore = "2030-01-01",
            updatedAfter = "should-fail",
            page = 1,
            pageSize = 50
        )
    }

}