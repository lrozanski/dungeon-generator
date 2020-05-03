package com.lrozanski.generator.dungeon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DungeonGeneratorApplication

fun main(args: Array<String>) {
    runApplication<DungeonGeneratorApplication>(*args)
}
