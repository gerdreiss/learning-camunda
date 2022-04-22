package com.github.gerdreiss.twitterqa.variables

import io.holunda.camunda.bpm.data.CamundaBpmDataKotlin

val TWEET_CONTENT  = CamundaBpmDataKotlin.customVariable<String>("tweetcontent")
val APPROVED  = CamundaBpmDataKotlin.customVariable<Boolean>("approved")
