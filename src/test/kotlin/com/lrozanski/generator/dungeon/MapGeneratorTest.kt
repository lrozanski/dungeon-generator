package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.Size
import org.junit.jupiter.api.Test

internal class MapGeneratorTest {

    @Test
    fun generateMap() {
        MapGenerator().generate(Size(64, 64))
    }
}
