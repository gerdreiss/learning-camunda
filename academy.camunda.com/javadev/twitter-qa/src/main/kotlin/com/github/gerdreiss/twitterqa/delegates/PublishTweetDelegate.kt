package com.github.gerdreiss.twitterqa.delegates

import com.github.gerdreiss.twitterqa.services.TwitterService
import com.github.gerdreiss.twitterqa.variables.TWEET_CONTENT
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component("publishTweetDelegate")
class PublishTweetDelegate @Autowired constructor(
    private val twitterService: TwitterService
) : JavaDelegate {

    override fun execute(execution: DelegateExecution) {
        val tweetContent = TWEET_CONTENT.from(execution).get()
        if ("Network error" == tweetContent) {
            throw RuntimeException("Simulated network error")
        }

        twitterService.publishTweet(tweetContent)
    }
}
