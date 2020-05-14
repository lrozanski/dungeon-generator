package com.lrozanski.generator.dungeon.corridor

import com.lrozanski.generator.dungeon.Grid
import com.lrozanski.generator.dungeon.RoomGenerator
import com.lrozanski.generator.dungeon.data.Room

class CorridorAppender(grid: Grid) {

    private val roomGenerator: RoomGenerator = RoomGenerator(grid)

    fun appendRoom(corridor: Corridor): Room? {
        val tries = 1000
        var room: Room?
        var i = 0

        do {
            room = roomGenerator.generateAtCorridorHead(corridor)
        } while (room == null && i++ < tries)

        return room
    }
}
