package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.Cell
import com.lrozanski.generator.dungeon.data.Size
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/")
class GeneratorResource {

    @PostMapping("/generate", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun generate(): List<Cell> {
        val mapGenerator = MapGenerator()
        mapGenerator.generate(Size(64, 64))

        return mapGenerator.findFilledCells()
    }
}
