package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.Cell
import com.lrozanski.generator.dungeon.data.Size
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/generator", produces = [MediaType.APPLICATION_JSON_VALUE])
class GeneratorResource {

    @PostMapping("/generate")
    fun generate(): List<Cell> {
        val mapGenerator = MapGenerator(Size(64, 64), 13..25)
        mapGenerator.generate()

        return mapGenerator.findFilledCells()
    }
}
