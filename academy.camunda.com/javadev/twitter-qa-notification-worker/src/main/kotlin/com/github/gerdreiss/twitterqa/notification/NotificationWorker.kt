package com.github.gerdreiss.twitterqa.notification

import org.camunda.bpm.client.ExternalTaskClient
import java.util.*

fun main() {
    ExternalTaskClient.create()
        .baseUrl("http://localhost:8080/engine-rest")
        .asyncResponseTimeout(20000)
        .lockDuration(10000)
        .maxTasks(1)
        .build()
        .subscribe("notification")
        .handler { externalTask, externalTaskService ->
            val content: String? = externalTask.getVariable("tweetcontent")
            println("Tweet rejected: $content")
            externalTaskService.complete(externalTask, mapOf("notificationTimestamp" to Date()))
        }
        .open()
}