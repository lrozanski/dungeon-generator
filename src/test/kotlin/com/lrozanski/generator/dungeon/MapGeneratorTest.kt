package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.Size
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

internal class MapGeneratorTest {

    private val mapVisualizer: MapVisualizer = MapVisualizer()

    @Test
    @Disabled
    fun generateMap() {
        val start = Instant.now()
        val mapGenerator = MapGenerator(Size(64, 64), 13..25)
        mapGenerator.generate()

        println(Duration.between(start, Instant.now()))
        mapVisualizer.createImage(mapGenerator.grid, 8)
    }
}
