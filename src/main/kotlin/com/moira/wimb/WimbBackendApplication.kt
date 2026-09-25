package com.moira.wimb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WimbBackendApplication

fun main(args: Array<String>) {
    runApplication<WimbBackendApplication>(*args)
}
