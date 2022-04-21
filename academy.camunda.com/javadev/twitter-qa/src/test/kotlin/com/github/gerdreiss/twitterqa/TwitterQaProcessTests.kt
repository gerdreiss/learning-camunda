package com.github.gerdreiss.twitterqa

import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.random.Random

@ExtendWith(ProcessEngineCoverageExtension::class)
class TwitterQaProcessTests {

    @Test
    @Deployment(resources = ["processes/twitter-qa.bpmn"])
    fun testHappyPath() {
        val variables = mapOf(
            "approved" to true,
            "content" to "Exercise 4 test - 0x" + Random.nextBits(16)
        )
        val processInstance = BpmnAwareTests.runtimeService().startProcessInstanceByKey("TwitterQAProcess", variables)
        ProcessEngineTests.assertThat(processInstance).isEnded
    }

}
