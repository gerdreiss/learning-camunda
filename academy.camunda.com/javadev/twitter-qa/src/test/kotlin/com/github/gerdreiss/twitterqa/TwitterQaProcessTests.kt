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
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import kotlin.random.Random

private const val PROCESS_KEY = "TwitterQAProcess"

@ExtendWith(ProcessEngineCoverageExtension::class)
@Deployment(resources = ["processes/twitter-qa.bpmn"])
class TwitterQaProcessTests {

    @Mock
    var twitterService: TwitterService? = null

    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)
        Mocks.register("publishTweetDelegate", PublishTweetDelegate(twitterService!!))

        Mockito
            .`when`(twitterService?.publishTweet(anyString()))
            .thenReturn(Random.nextLong())
    }

    @Test
    fun `test approving a tweet`() {

        val processInstance = runtimeService()
            .startProcessInstanceByKey(
                PROCESS_KEY,
                mapOf(TWEET_CONTENT.name to tweetContent(4))
            )

        assertThat(processInstance).isStarted.isWaitingAt(findId("Review tweet"))

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
        execute(job()) // Execute publish tweet job

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
            .createProcessInstanceByKey(PROCESS_KEY)
            .setVariable(TWEET_CONTENT.name, tweetContent(8))
            .setVariable(APPROVED.name, false)
            .startAfterActivity(findId("Review tweet"))
            .execute()

        assertThat(processInstance)
            .isWaitingAt(findId("Notify user of rejection"))
            .externalTask()
            .hasTopicName("notification")
        complete(externalTask())

        //val taskList = taskService()
        //    .createTaskQuery()
        //    .taskCandidateGroup("management")
        //    .processInstanceId(processInstance.id)
        //    .list()
        //assertThat(taskList).isNotNull.hasSize(1)
        //taskService().complete(taskList[0].id, mapOf(APPROVED.name to false))

        assertThat(processInstance).isEnded.hasPassed(findId("Tweet rejected"))
    }

    @Test
    fun `test superuser tweet`() {

        val processInstance = runtimeService()
            .createMessageCorrelation("superuserTweet")
            .setVariable(TWEET_CONTENT.name, tweetContent(11))
            .correlateWithResult()
            .processInstance

        assertThat(processInstance).isStarted

        execute(job()) // Execute publish tweet job

        assertThat(processInstance).isEnded.hasPassed(findId("Tweet published"))
    }

    @Test
    fun `test tweet withdrawn`() {

        val tweetContent = tweetContent(11)

        val processInstance = runtimeService()
            .startProcessInstanceByKey(
                PROCESS_KEY,
                mapOf(TWEET_CONTENT.name to tweetContent)
            )

        assertThat(processInstance).isStarted.isWaitingAt(findId("Review tweet"))

        runtimeService()
            .createMessageCorrelation("tweetWithdrawn")
            .processInstanceVariableEquals(TWEET_CONTENT.name, tweetContent)
            .correlateWithResult()

        assertThat(processInstance).isEnded.hasPassed("TweetWithdrawnEndEvent")
    }

    private fun tweetContent(exercise: Int): String =
        "Exercise $exercise test - 0x" + Random.nextBits(16)

}
