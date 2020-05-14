package com.lrozanski.generator.dungeon.corridor

import com.lrozanski.generator.dungeon.Grid
import com.lrozanski.generator.dungeon.MapGenerator
import com.lrozanski.generator.dungeon.data.Connector

class CorridorWalker(private val generator: MapGenerator, private val grid: Grid) {

    private val corridorAppender = CorridorAppender(grid)

    fun walk(connector: Connector): Boolean {
        var previousDirection: Direction? = connector.direction
        val corridor = Corridor(grid, connector.position)
        var i = 0

        do {
            previousDirection = corridor.step(previousDirection)
        } while (previousDirection != null && i++ < 1000)

        if (corridor.cells.size <= 1) {
//            debug(connector.position, Color(0, 255, 255, 255))
            connector.unused = true
            return false
        }
        val newRoom = corridorAppender.appendRoom(corridor)
        if (newRoom == null) {
            connector.unused = true
//            debug(connector.position, Color(255, 255, 255, 255))
            return false
        }
        with(corridor) {
            placeFloor()
            placeWalls()
        }
        generator.rooms.add(newRoom)
        newRoom
            .connectors
            .filter { it != newRoom.connectors.first() }
            .forEach { walk(it) }
        return true
    }
}
