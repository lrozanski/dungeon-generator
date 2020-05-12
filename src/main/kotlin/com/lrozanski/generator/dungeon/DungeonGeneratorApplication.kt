package com.lrozanski.generator.dungeon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DungeonGeneratorApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<DungeonGeneratorApplication>(*args)
        }
    }
}
