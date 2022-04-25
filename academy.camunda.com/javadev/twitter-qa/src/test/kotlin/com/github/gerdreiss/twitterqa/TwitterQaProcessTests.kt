package com.github.gerdreiss.twitterqa

import com.github.gerdreiss.twitterqa.delegates.PublishTweetDelegate
import com.github.gerdreiss.twitterqa.services.TwitterService
import com.github.gerdreiss.twitterqa.variables.APPROVED
import com.github.gerdreiss.twitterqa.variables.TWEET_CONTENT
import org.assertj.core.api.Assertions.*
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.random.Random

@ExtendWith(ProcessEngineCoverageExtension::class)
@Deployment(resources = ["processes/twitter-qa.bpmn"])
class TwitterQaProcessTests {

    @Mock
    var twitterService: TwitterService? = null

    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)
        Mocks.register("publishTweetDelegate", PublishTweetDelegate(twitterService!!))
    }

    @Test
    fun `test approving a tweet`() {

        Mockito
            .`when`(twitterService!!.publishTweet("tweeeet!"))
            .thenReturn(1L)

        val processInstance = runtimeService()
            .startProcessInstanceByKey(
                "TwitterQAProcess",
                mapOf(TWEET_CONTENT.name to tweetContent())
            )

        assertThat(processInstance).isWaitingAt(findId("Review tweet"))

        val taskList = taskService()
            .createTaskQuery()
            .taskCandidateGroup("management")
            .processInstanceId(processInstance.id)
            .list()

        assertThat(taskList).isNotNull.hasSize(1)

        taskService().complete(taskList[0].id, mapOf(APPROVED.name to true))

        // the long version
        //val jobList = jobQuery()
        //    .processInstanceId(processInstance.id)
        //    .list()
        //assertThat(jobList).isNotNull.hasSize(1)
        //execute(jobList.first())
        // the short version
        execute(job())

        assertThat(processInstance).hasPassed(findId("Tweet published"))
        assertThat(processInstance).isEnded
    }

    @Test
    fun `test rejecting a tweet`() {

        //val processInstance = runtimeService()
        //    .startProcessInstanceByKey(
        //        "TwitterQAProcess",
        //        mapOf(TWEET_CONTENT.name to tweetContent())
        //    )

        val processInstance = runtimeService()
            .createProcessInstanceByKey("TwitterQAProcess")
            .setVariable(TWEET_CONTENT.name, tweetContent())
            .setVariable(APPROVED.name, false)
            .startAfterActivity(findId("Review tweet"))
            .execute()

        //val taskList = taskService()
        //    .createTaskQuery()
        //    .taskCandidateGroup("management")
        //    .processInstanceId(processInstance.id)
        //    .list()
        //
        //assertThat(taskList).isNotNull.hasSize(1)
        //
        //taskService().complete(taskList[0].id, mapOf(APPROVED.name to false))

        assertThat(processInstance).hasPassed(findId("Tweet rejected"))
        assertThat(processInstance).isEnded
    }

    private fun tweetContent(): String = "Exercise 5 test - 0x" + Random.nextBits(16)

}
