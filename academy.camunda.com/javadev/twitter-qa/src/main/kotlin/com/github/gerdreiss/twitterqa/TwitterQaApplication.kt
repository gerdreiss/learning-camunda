package com.github.gerdreiss.twitterqa

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableProcessApplication("twitter-qa")
class TwitterQaApplication

fun main(args: Array<String>) {
    runApplication<TwitterQaApplication>(*args)
}
