package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.Cell
import com.lrozanski.generator.dungeon.data.Room
import com.lrozanski.generator.dungeon.data.Size

class MapGenerator {

    private val roomGenerator: RoomGenerator = RoomGenerator()
    private val mapVisualizer: MapVisualizer = MapVisualizer()
    private val rooms: MutableList<Room> = mutableListOf()

    private lateinit var grid: Grid

    fun generate(size: Size) {
        grid = Grid(size)

        roomGenerator
            .generate(grid, 2)
            .apply { rooms.add(this!!) }

        val corridorWalker = CorridorWalker()
        rooms.forEach { room ->
            room.connectors.forEach { corridorWalker.walk(grid, room, it) }
        }

        mapVisualizer.createImage(grid, 8)
    }

    fun findFilledCells() = grid.filter(Cell::isNotEmpty)
}
