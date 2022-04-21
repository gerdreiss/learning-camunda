package com.github.gerdreiss.twitterqa.delegate

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

@Component
class CreateTweetDelegate : JavaDelegate {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun execute(execution: DelegateExecution) {
        val content = "I did it! Cheers G"
        logger.info("Publishing tweet: '$content'")
        val accessToken = AccessToken(
            "220324559-CO8TfUmrcoCrvFHP4TacgToN5hLC8cMw4n2EwmHo",
            "WvVureFv5TBWTGhESgGe3fqZM7XbGMuyIhxB84zgcoUER"
        )
        val twitter = TwitterFactory().instance
        twitter.setOAuthConsumer("lRhS80iIXXQtm6LM03awjvrvk", "gabtxwW8lnSL9yQUNdzAfgBOgIMSRqh7MegQs79GlKVWF36qLS")
        twitter.oAuthAccessToken = accessToken
        //twitter.updateStatus(content)
    }
}