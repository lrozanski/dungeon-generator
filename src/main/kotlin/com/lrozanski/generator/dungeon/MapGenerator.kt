package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.corridor.CorridorWalker
import com.lrozanski.generator.dungeon.data.Cell
import com.lrozanski.generator.dungeon.data.Room
import com.lrozanski.generator.dungeon.data.Size

class MapGenerator(val size: Size, private val roomCount: IntRange) {

    val grid: Grid = Grid(size)
    val rooms: MutableList<Room> = mutableListOf()

    private val roomGenerator: RoomGenerator = RoomGenerator(grid)
    private val corridorWalker = CorridorWalker(this, grid)

    fun generate() {
        var tries = 0
        while (rooms.size !in roomCount && tries++ < 1000) {
            grid.clear()
            rooms.clear()

            roomGenerator
                .generateStartRoom(2)
                ?.apply { rooms.add(this) }
                ?.apply { this.connectors.forEach { corridorWalker.walk(it) } }

            rooms.forEach { room ->
                room.cleanUpConnectors()
                room.connectors.forEach { connector ->
                    grid[connector.position] = Cell(connector.position, CellType.DOOR)
                }
            }
        }
    }

    fun findFilledCells() = grid.filter(Cell::isNotEmpty)
}
