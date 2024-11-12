package com.neon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TermboltApplication

fun main(args: Array<String>) {
    runApplication<TermboltApplication>(*args)
}
