package com.lrozanski.generator.dungeon.map.corridor

import com.lrozanski.generator.dungeon.RoomGenerator
import com.lrozanski.generator.dungeon.map.Grid
import com.lrozanski.generator.dungeon.map.Mask
import com.lrozanski.generator.dungeon.map.data.Room

class CorridorAppender(grid: Grid) {

    private val roomGenerator: RoomGenerator = RoomGenerator(grid)

    fun appendRoom(corridor: Corridor, corridorMask: Mask): Room? {
        var room: Room?
        var tries = 0

        do {
            room = roomGenerator.generateAtCorridorHead(corridor, corridorMask)
        } while (room == null && tries++ < 100)

        return room
    }
}
