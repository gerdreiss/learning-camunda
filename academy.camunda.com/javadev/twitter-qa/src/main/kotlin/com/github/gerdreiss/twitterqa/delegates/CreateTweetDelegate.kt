package com.github.gerdreiss.twitterqa.delegates

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

@Component("createTweetDelegate")
class CreateTweetDelegate : JavaDelegate {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    override fun execute(execution: DelegateExecution) {
        val content = execution.getVariable("content") as String
        logger.info("Publishing tweet: '$content'")
        val accessToken = AccessToken(
            "220324559-CO8TfUmrcoCrvFHP4TacgToN5hLC8cMw4n2EwmHo",
            "WvVureFv5TBWTGhESgGe3fqZM7XbGMuyIhxB84zgcoUER"
        )
        val twitter = TwitterFactory().instance
        twitter.setOAuthConsumer("lRhS80iIXXQtm6LM03awjvrvk", "gabtxwW8lnSL9yQUNdzAfgBOgIMSRqh7MegQs79GlKVWF36qLS")
        twitter.oAuthAccessToken = accessToken
        twitter.updateStatus(content)
    }
}
