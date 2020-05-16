package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.corridor.Corridor
import com.lrozanski.generator.dungeon.map.Grid
import com.lrozanski.generator.dungeon.map.Mask
import com.lrozanski.generator.dungeon.map.data.*
import kotlin.random.Random

class RoomGenerator(private val grid: Grid) {

    private val minSize = 4
    private val maxSize = 8

    fun generateStartRoom(roomMargin: Int = 1): Room? {
        val tries = 100
        val minArea = 10
        var room: Room
        var i = 0

        do {
            room = generateRoom()
        } while (room.rect.grow(roomMargin).isNotEmpty() && room.rect.area() < minArea && !room.rect.isWithinBounds() && i++ < tries)
        if (i >= tries) {
            return null
        }
        room.placeFloor()
        room.placeWalls()
        room.placeConnectors()

        return room
    }

    private fun generateRoom(): Room {
        val size = Size.random(minSize, maxSize, minSize, maxSize)
        val position = Position.random(0, grid.size.w - size.w + 1, 0, grid.size.h - size.h + 1)

        return Room(GridRect(grid, position, size))
    }

    fun generateAtCorridorHead(corridor: Corridor, corridorMask: Mask, roomMargin: Int = 2): Room? {
        val head = corridor.head()
        val rect = GridRect(grid, head, Size.random(minSize, maxSize, minSize, maxSize))
        val room = Room(rect)
        val connectorDirection = corridor.direction.opposite()
        val connectorPosition = room.edge(connectorDirection).random()

        val adjustedRect = rect.offset(head - connectorPosition)
        if (!adjustedRect.isWithinBounds()) {
            return null
        }
        val marginRect = adjustedRect.grow(roomMargin)
        if (marginRect.isNotEmpty()) {
            return null
        }
        val tempMask = corridorMask.copy()
        repeat(1) {
            val cells = corridor.growPerpendicular(corridor.head(it), corridor.direction, 1)
            tempMask.subtract(cells)
        }
        if (!tempMask.isRectangleEmpty(adjustedRect.position, adjustedRect.size)) {
            return null
        }

        val adjustedRoom = Room(adjustedRect, Random.nextInt(2, 5))
        adjustedRoom.addConnector(Connector(head, connectorDirection))
        return adjustedRoom.apply {
            placeFloor()
            placeWalls()
            placeConnectors()
        }
    }
}
