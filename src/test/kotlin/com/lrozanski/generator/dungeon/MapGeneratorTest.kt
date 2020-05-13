package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.Size
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.jupiter.api.Test

internal class MapGeneratorTest {

    @Test
    @Ignore
    fun generateMap() {
        MapGenerator(Size(64, 64)).generate()
    }
}
