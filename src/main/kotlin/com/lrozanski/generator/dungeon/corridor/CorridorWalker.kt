package com.lrozanski.generator.dungeon.corridor

import com.lrozanski.generator.dungeon.MapGenerator
import com.lrozanski.generator.dungeon.map.Grid
import com.lrozanski.generator.dungeon.map.Mask
import com.lrozanski.generator.dungeon.map.data.Connector
import com.lrozanski.generator.dungeon.map.data.GridRect
import com.lrozanski.generator.dungeon.map.data.Size
import com.lrozanski.generator.dungeon.visualization.MapVisualizer
import kotlin.random.Random

class CorridorWalker(private val generator: MapGenerator, private val grid: Grid) {

    private val corridorAppender = CorridorAppender(grid)
    private val corridorMask = Mask(grid.size)

    fun clear() {
        corridorMask.fill { _, _ -> Mask.EMPTY }
//        MapVisualizer.createImage(corridorMask, 8)
    }

    fun walk(connector: Connector): Boolean {
        corridorMask.fill { _, _ -> Mask.EMPTY }
        generator.rooms.forEach { corridorMask.add(it.rect) }
        generator.corridors.forEach { corridor ->
            corridor.cells
                .subList(1, corridor.cells.size - 2)
                .map { position -> GridRect(grid, position, Size(1, 1)).grow(1) }
                .forEach { rect -> corridorMask.add(rect) }
        }
//        MapVisualizer.createImage(corridorMask, 8)

        var previousDirection: Direction? = connector.direction
        val corridor = Corridor(grid, connector.forward(1), Random.nextInt(3, 15))
        var i = 0

        do {
            previousDirection = corridor.step(corridorMask, previousDirection, weight = 0.6)
        } while (previousDirection != null && i++ < 1000)

        if (corridor.cells.size <= 2) {
            connector.unused = true
            return false
        }

        val newRoom = corridorAppender.appendRoom(corridor, corridorMask)
        if (newRoom == null) {
            connector.unused = true
            return false
        }
        with(corridor) {
            placeFloor()
            placeWalls()
        }
//        MapVisualizer.createImage(grid, 8)

        generator.corridors.add(corridor)
        generator.rooms.add(newRoom)

        newRoom
            .connectors
            .filter { it != newRoom.connectors.first() }
            .forEach {
                if (walk(it)) {
//                    MapVisualizer.createImage(grid, 8)
                }
            }

//        MapVisualizer.createImage(corridorMask, 8)
        return true
    }
}
