package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.corridor.Corridor
import com.lrozanski.generator.dungeon.data.*
import java.awt.Color

class RoomGenerator(private val grid: Grid) {

    fun generateStartRoom(roomMargin: Int = 1): Room? {
        val tries = 1000
        val minArea = 10
        var room: Room
        var i = 0

        do {
            room = generateRoom()
        } while ((!room.rect.grow(roomMargin).isEmpty() && i++ < tries) || room.rect.area() < minArea || !room.rect.isWithinBounds())
        if (i >= tries) {
            return null
        }
        room.placeFloor()
        room.placeWalls()
        room.placeConnectors()
//        room.rect.outline().forEach { debug(Position(it.x, it.y), Color(255, 255, 0, 100)) }

        return room
    }

    private fun generateRoom(): Room {
        val size = Size.random(8, 13, 8, 13)
        val position = Position.random(0, grid.size.w - size.w + 1, 0, grid.size.h - size.h + 1)

        return Room(GridRect(grid, position, size))
    }

    fun generateAtCorridorHead(corridor: Corridor, roomMargin: Int = 2): Room? {
        val head = corridor.head()
        val rect = GridRect(grid, head, Size.random(8, 13, 8, 13))
        val room = Room(rect)
        val connectorDirection = corridor.direction.opposite()
        val connectorPosition = room.edge(connectorDirection).random()

        val adjustedRect = rect.offset(head - connectorPosition)
        if (!adjustedRect.isWithinBounds()) {
            return null
        }

        val adjustedRoom = Room(adjustedRect, 2)
        val connector = Connector(head, connectorDirection)
        adjustedRoom.addConnector(connector)

        if (adjustedRect.grow(roomMargin).isNotEmpty()) {
            return null
        }
//        rect.outline().forEach { debug(Position(it.x, it.y), Color(0, 0, 0, 150)) }
//        adjustedRect.outline().forEach { debug(Position(it.x, it.y), Color(255, 0, 255, 150)) }
//        debug(head, Color(0, 255, 0, 255))
//        debug(connectorPosition, Color(150, 255, 255, 255))
//        debug(connector.position, Color(255, 255, 255, 255))

        return adjustedRoom.apply {
            placeFloor()
            placeWalls()
            placeConnectors()
        }
    }
}
