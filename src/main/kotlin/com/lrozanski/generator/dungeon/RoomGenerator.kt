package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.GridRect
import com.lrozanski.generator.dungeon.data.Position
import com.lrozanski.generator.dungeon.data.Room
import com.lrozanski.generator.dungeon.data.Size

class RoomGenerator {

    fun generate(grid: Grid, roomMargin: Int = 1): Room? {
        var room = generateRoom(grid)
        val minArea = 10
        var i = 0

        while ((!room.rect.grow(roomMargin).isEmpty() && i++ < 1000) || room.rect.area() < minArea) {
            room = generateRoom(grid)
        }
        if (i >= 1000) {
            return null
        }
        room.placeFloor()
        room.placeWalls()
        room.placeConnectors(2)

        return room
    }

    fun createRoom(gridRect: GridRect, roomMargin: Int = 1) : Room? {
        if (!gridRect.grow(roomMargin).isEmpty()) {
            return null
        }
        val room = Room(gridRect)
        room.placeFloor()
        room.placeWalls()
        room.placeConnectors(2)

        return room
    }

    private fun generateRoom(grid: Grid): Room {
        val size = Size.random(6, 13, 6, 13)
        val position = Position.random(0, grid.size.w - size.w, 0, grid.size.h - size.h)

        return Room(GridRect(grid, position, size))
    }
}
