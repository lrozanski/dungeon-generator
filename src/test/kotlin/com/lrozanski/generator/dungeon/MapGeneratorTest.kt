package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.Size
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class MapGeneratorTest {

    @Test
    @Disabled
    fun generateMap() {
        MapGenerator(Size(64, 64)).generate()
    }
}
