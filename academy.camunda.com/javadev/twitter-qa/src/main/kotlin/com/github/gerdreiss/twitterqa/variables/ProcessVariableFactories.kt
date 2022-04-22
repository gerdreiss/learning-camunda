package com.github.gerdreiss.twitterqa.variables

import io.holunda.camunda.bpm.data.CamundaBpmDataKotlin

val TWEET_CONTENT  = CamundaBpmDataKotlin.customVariable<String>("tweet-content")
