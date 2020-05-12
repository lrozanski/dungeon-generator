package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.corridor.CorridorWalker
import com.lrozanski.generator.dungeon.data.Cell
import com.lrozanski.generator.dungeon.data.Room
import com.lrozanski.generator.dungeon.data.Size

val ROOMS: MutableList<Room> = mutableListOf()

class MapGenerator(val size: Size) {

    private val grid: Grid = Grid(size)

    private val roomGenerator: RoomGenerator = RoomGenerator(grid)
    private val corridorWalker = CorridorWalker(grid)
    private val mapVisualizer: MapVisualizer = MapVisualizer()

    fun generate() {
        roomGenerator
            .generateStartRoom(2)
            .apply { ROOMS.add(this!!) }
            .apply { this!!.connectors.forEach { corridorWalker.walk(it) } }

        ROOMS.forEach { room ->
            room.cleanUpConnectors()
            room.connectors.forEach { connector ->
                grid[connector.position] = Cell(connector.position, CellType.DOOR)
            }
        }

//        mapVisualizer.createImage(grid, 8)
    }

    fun findFilledCells() = grid.filter(Cell::isNotEmpty)
}
