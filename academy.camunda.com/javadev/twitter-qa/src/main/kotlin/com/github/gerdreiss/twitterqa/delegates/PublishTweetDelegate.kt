package com.github.gerdreiss.twitterqa.delegates

import com.github.gerdreiss.twitterqa.variables.TWEET_CONTENT
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

@Component("publishTweetDelegate")
class PublishTweetDelegate : JavaDelegate {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(PublishTweetDelegate::class.java)
    }

    override fun execute(execution: DelegateExecution) {
        val tweetContent = TWEET_CONTENT.from(execution).get()
        if ("Network error" == tweetContent) {
            throw RuntimeException("Simulated network error")
        }

        logger.info("Publishing tweet: '$tweetContent'")
        val accessToken = AccessToken(
            "220324559-CO8TfUmrcoCrvFHP4TacgToN5hLC8cMw4n2EwmHo",
            "WvVureFv5TBWTGhESgGe3fqZM7XbGMuyIhxB84zgcoUER"
        )
        val twitter = TwitterFactory().instance
        twitter.setOAuthConsumer(
            "lRhS80iIXXQtm6LM03awjvrvk",
            "gabtxwW8lnSL9yQUNdzAfgBOgIMSRqh7MegQs79GlKVWF36qLS"
        )
        twitter.oAuthAccessToken = accessToken
        twitter.updateStatus(tweetContent)
    }
}
