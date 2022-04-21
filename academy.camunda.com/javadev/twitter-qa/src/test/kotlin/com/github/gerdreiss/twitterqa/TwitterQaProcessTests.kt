package com.github.gerdreiss.twitterqa

import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ProcessEngineExtension::class)
class TwitterQaProcessTests {

    @Test
    @Deployment(resources = ["processes/twitter-qa.bpmn"])
    fun testHappyPath() {
        val variables = mapOf("approved" to true)
        val processInstance = BpmnAwareTests.runtimeService().startProcessInstanceByKey("TwitterQAProcess", variables)
        ProcessEngineTests.assertThat(processInstance).isEnded
    }

}
