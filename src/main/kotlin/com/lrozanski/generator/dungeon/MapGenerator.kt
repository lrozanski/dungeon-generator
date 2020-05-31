package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.map.Grid
import com.lrozanski.generator.dungeon.map.ReplayGrid
import com.lrozanski.generator.dungeon.map.corridor.Corridor
import com.lrozanski.generator.dungeon.map.corridor.CorridorWalker
import com.lrozanski.generator.dungeon.map.data.Cell
import com.lrozanski.generator.dungeon.map.data.Room
import com.lrozanski.generator.dungeon.map.data.Size
import com.lrozanski.generator.dungeon.visualization.MapVisualizer
import kotlin.random.Random

class MapGenerator(val size: Size, private val roomCount: IntRange) {

    val grid: Grid = ReplayGrid(size)
    val rooms: MutableList<Room> = mutableListOf()
    val corridors: MutableSet<Corridor> = mutableSetOf()

    private val roomGenerator: RoomGenerator = RoomGenerator(grid)
    private val corridorWalker = CorridorWalker(this, grid)

    fun generate() {
        var tries = 0
        while (rooms.size !in roomCount && tries++ < 100) {
            corridorWalker.clear()
            MapVisualizer.clearGifFrames()

            grid.fill { x, y -> Cell(x, y, CellType.EMPTY) }
            corridors.clear()
            rooms.clear()

            roomGenerator
                .generateStartRoom(2)
                ?.apply { rooms.add(this) }
                ?.apply { this.connectors.forEach { corridorWalker.walk(it) } }

            corridors.forEach {
                if (Random.nextBoolean()) return@forEach
                val fork = it.fork() ?: return@forEach

                val forked = corridorWalker.walk(fork, true)
                if (forked) {
                    grid[fork.position] = Cell(fork.position.x, fork.position.y, CellType.DOOR, secret = true)
                }
            }

            rooms.forEach { room ->
                room.cleanUpConnectors()
                room.connectors.forEach { connector ->
                    grid[connector.position] = Cell(connector.position, CellType.DOOR)
                }
            }

            corridors.random().placeStairs(CellType.STAIRS_UP)
            corridors.random().placeStairs(CellType.STAIRS_DOWN)
        }
    }

    fun findFilledCells() = grid.filter(Cell::isNotEmpty)
}
