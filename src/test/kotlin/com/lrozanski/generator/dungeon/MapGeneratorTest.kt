package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.map.ReplayGrid
import com.lrozanski.generator.dungeon.map.data.Size
import com.lrozanski.generator.dungeon.visualization.MapVisualizer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Duration
import java.time.Instant
import javax.imageio.ImageIO

internal class MapGeneratorTest {

    @Test
    @Disabled
    fun generateMap() {
        val start = Instant.now()
        val mapGenerator = MapGenerator(Size(64, 64), 8..100)
        mapGenerator.generate()

        println(Duration.between(start, Instant.now()))

        val shrunkImage = MapVisualizer.createImage(mapGenerator.grid.shrink(), 8, false)
        ImageIO.write(shrunkImage, "png", File("src/main/resources/map.png"))

        if (mapGenerator.grid is ReplayGrid) {
            (mapGenerator.grid as ReplayGrid).writeReplay(File("src/main/resources/replay.json"))
        }
    }
}
