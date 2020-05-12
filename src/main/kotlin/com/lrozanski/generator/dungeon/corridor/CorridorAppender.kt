package com.lrozanski.generator.dungeon.corridor

import com.lrozanski.generator.dungeon.Grid
import com.lrozanski.generator.dungeon.ROOMS
import com.lrozanski.generator.dungeon.RoomGenerator
import com.lrozanski.generator.dungeon.data.Room

class CorridorAppender(grid: Grid) {

    private val roomGenerator: RoomGenerator = RoomGenerator(grid)

    fun appendRoom(corridor: Corridor): Room? {
//        if (ROOMS.size > 5) {
//            return null
//        }
        val tries = 1000
        var room: Room?
        var i = 0

        do {
            room = roomGenerator.generateAtCorridorHead(corridor)
        } while (room == null && i++ < tries)

        return room
    }
}
