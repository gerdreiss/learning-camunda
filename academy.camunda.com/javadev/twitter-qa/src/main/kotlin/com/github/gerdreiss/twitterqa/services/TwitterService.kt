package com.github.gerdreiss.twitterqa.services

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken

private const val TWITTER_ACCESS_TOKEN = "220324559-CO8TfUmrcoCrvFHP4TacgToN5hLC8cMw4n2EwmHo"
private const val TWITTER_ACCESS_SECRET = "WvVureFv5TBWTGhESgGe3fqZM7XbGMuyIhxB84zgcoUER"
private const val TWITTER_ACCESS_OAUTH_KEY = "lRhS80iIXXQtm6LM03awjvrvk"
private const val TWITTER_ACCESS_OATH_SECRET = "gabtxwW8lnSL9yQUNdzAfgBOgIMSRqh7MegQs79GlKVWF36qLS"

@Service
class TwitterService {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    }

    fun publishTweet(tweetContent: String): Long {
        logger.info("Publishing tweet: '$tweetContent'")
        val twitter = TwitterFactory().instance
        twitter.setOAuthConsumer(TWITTER_ACCESS_OAUTH_KEY, TWITTER_ACCESS_OATH_SECRET)
        twitter.oAuthAccessToken = AccessToken(TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_SECRET)
        return twitter.updateStatus(tweetContent).id
    }
}
