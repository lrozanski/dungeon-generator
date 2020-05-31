package com.lrozanski.generator.dungeon.map.corridor

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.MapGenerator
import com.lrozanski.generator.dungeon.map.Grid
import com.lrozanski.generator.dungeon.map.Mask
import com.lrozanski.generator.dungeon.map.data.*
import com.lrozanski.generator.dungeon.visualization.debug
import java.awt.Color
import kotlin.random.Random

class CorridorWalker(private val generator: MapGenerator, private val grid: Grid) {

    private val corridorAppender = CorridorAppender(grid)
    private val corridorMask = Mask(grid.size)

    fun clear() {
        corridorMask.fill { _, _ -> Mask.EMPTY }
//        MapVisualizer.createImage(corridorMask, 8)
    }

    fun walk(connector: Connector, fork: Boolean = false): Boolean {
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
        val corridor = Corridor(grid, connector.forward(1), fork, Random.nextInt(3, 15))
        var i = 0

        do {
            previousDirection = corridor.step(corridorMask, previousDirection, weight = if (fork) 0.6 else 0.9)
        } while (previousDirection != null && i++ < 1000)

        if (corridor.cells.size <= 2) {
            connector.unused = true
            return false
        }

        val head = corridor.head()
        val forkX = head.x + corridor.direction.position.x
        val forkY = head.y + connector.direction.position.y

        if (fork) {
            if (grid[forkX, forkY]?.type == CellType.WALL) {
                grid[forkX, forkY] = Cell(forkX, forkY, CellType.DOOR, secret = true)
            }
//            debug(Position(forkX, forkY), Color(1f, 0f, 0f, 0.6f))
            corridor.placeFloor(true)
            corridor.placeWalls(true)
//            corridor.cells.forEach { debug(it, Color.RED) }
            return true
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
        with(newRoom) {
            placeFloor()
            placeWalls()
            placeConnectors()
        }
//        MapVisualizer.createImage(grid, 8)

        generator.corridors.add(corridor)
        generator.rooms.add(newRoom)

        newRoom
            .takeIf { !fork }
            ?.connectors
            ?.filter { it != newRoom.connectors.first() }
            ?.forEach {
                if (walk(it)) {
//                    MapVisualizer.createImage(grid, 8)
                }
            }

//        MapVisualizer.createImage(corridorMask, 8)
        return true
    }
}
