package com.github.gerdreiss.twitterqa

import arrow.core.left
import arrow.core.right
import com.github.gerdreiss.twitterqa.delegates.PublishTweetDelegate
import com.github.gerdreiss.twitterqa.services.TwitterService
import com.github.gerdreiss.twitterqa.variables.APPROVED
import com.github.gerdreiss.twitterqa.variables.EMAIL
import com.github.gerdreiss.twitterqa.variables.TWEET_CONTENT
import org.assertj.core.api.Assertions.*
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import twitter4j.TwitterException
import kotlin.random.Random

private const val PROCESS_KEY = "TwitterQAWithBusinessRuleTaskProcess"
private const val JAKOB_EMAIL = "jakob.freund@camunda.com"

@ExtendWith(ProcessEngineCoverageExtension::class)
@Deployment(resources = ["processes/tweet-approval.dmn", "processes/twitter-qa-with-rule-task.bpmn"])
class TwitterQaBusinessRuleProcessTests {

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
            .`when`(twitterService?.publishTweet(anyString()))
            .thenReturn(Random.nextLong().right())

        val processInstance = runtimeService()
            .startProcessInstanceByKey(
                PROCESS_KEY,
                mapOf(
                    EMAIL.name to JAKOB_EMAIL,
                    TWEET_CONTENT.name to tweetContent(12)
                )
            )

        assertThat(processInstance).isStarted.isWaitingAt("PublishTweetTask")

        execute(job()) // Execute publish tweet job

        assertThat(processInstance).hasPassed(findId("Review tweet business rule"))
        assertThat(processInstance).isEnded
    }

    @Test
    fun `test rejecting a tweet`() {

        val processInstance = runtimeService()
            .createProcessInstanceByKey(PROCESS_KEY)
            .setVariable(TWEET_CONTENT.name, tweetContent(8))
            .setVariable(APPROVED.name, false)
            .startAfterActivity(findId("Review tweet business rule"))
            .execute()

        assertThat(processInstance)
            .isWaitingAt(findId("Notify user of rejection"))
            .externalTask()
            .hasTopicName("notification")
        complete(externalTask())

        assertThat(processInstance).isEnded.hasPassed(findId("Tweet rejected"))
    }

    @Test
    fun `test superuser tweet`() {

        Mockito
            .`when`(twitterService?.publishTweet(anyString()))
            .thenReturn(Random.nextLong().right())

        val processInstance = runtimeService()
            .createMessageCorrelation("superuserTweet")
            .setVariable(TWEET_CONTENT.name, tweetContent(12))
            .correlateWithResult()
            .processInstance

        assertThat(processInstance).isStarted

        execute(job()) // Execute publish tweet job

        assertThat(processInstance).isEnded.hasPassed(findId("Tweet published"))
    }

    @Test
    fun `test duplicate tweet`() {

        Mockito
            .`when`(twitterService?.publishTweet(anyString()))
            .thenReturn(TwitterException("Duplicate tweet").left())

        val processInstance = runtimeService()
            .createMessageCorrelation("superuserTweet")
            .setVariable(TWEET_CONTENT.name, tweetContent(12))
            .correlateWithResult()
            .processInstance

        assertThat(processInstance).isStarted

        execute(job()) // Execute publish tweet job

        assertThat(processInstance).isWaitingAt(findId("Amend Tweet"))
    }

    @Test
    fun `test tweet from Jakob`() {
        val decisionResult = decisionService()
            .evaluateDecisionTableByKey(
                "TweetApprovalDecision",
                mapOf(
                    EMAIL.name to JAKOB_EMAIL,
                    TWEET_CONTENT.name to "this should be published"
                )
            )
            .firstResult

        assertTrue(decisionResult.getEntry<Boolean>("approved"))
    }

    private fun tweetContent(exercise: Int): String =
        "Exercise $exercise test - 0x" + Random.nextBits(16)

}
