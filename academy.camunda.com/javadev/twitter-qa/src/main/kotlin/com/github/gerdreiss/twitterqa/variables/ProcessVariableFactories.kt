package com.github.gerdreiss.twitterqa.variables

import io.holunda.camunda.bpm.data.CamundaBpmDataKotlin.customVariable

val EMAIL = customVariable<String>("email")
val TWEET_CONTENT = customVariable<String>("tweet_content")
val APPROVED = customVariable<Boolean>("approved")
