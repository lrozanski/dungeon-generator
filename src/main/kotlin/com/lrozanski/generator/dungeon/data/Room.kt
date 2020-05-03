package com.lrozanski.generator.dungeon.data

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.Direction
import kotlin.random.Random

data class Connector(val position: Position, val direction: Direction)

data class Room(val rect: GridRect) {

    lateinit var connectors: List<Connector>

    fun placeFloor() = forEachCell {
        rect.grid[it.x, it.y] = Cell(it.x, it.y, CellType.FLOOR)
    }

    fun placeWalls() = forEachCell {
        val x = it.x
        val y = it.y

        if ((x == rect.position.x || x == rect.position.x + rect.size.w - 1)
            || (y == rect.position.y || y == rect.position.y + rect.size.h - 1)) {
            rect.grid[it.x, it.y] = Cell(it.x, it.y, CellType.WALL)
        }
    }

    fun placeConnectors(count: Int) {
        val positions = mutableListOf<Position>()

        for (i in count downTo 0) {
            var position: Position
            do {
                position = randomPointOnEdge()
            } while (positions.any { it.isAdjacent(position) })

            positions += position
        }
        connectors = positions
            .map { Connector(it, findDirection(it)) }
            .toList()

//        connectors.forEach {
//            rect.grid[it.position.x, it.position.y] = Cell(it.position.x, it.position.y, CellType.DOOR)
//        }
    }

    private fun forEachCell(action: (cell: Cell) -> Unit) = rect.grid.forEachCell(this) { action(it) }

    private fun randomPointOnEdge(): Position {
        var position: Position

        do {
            val horizontal = Random.nextBoolean()
            position = when {
                horizontal -> Position(
                    Random.nextInt(rect.position.x + 1, rect.position.x + 1 + rect.size.w - 2),
                    if (Random.nextBoolean()) rect.position.y else rect.position.y + rect.size.h - 1
                )
                else -> Position(
                    if (Random.nextBoolean()) rect.position.x else rect.position.x + rect.size.w - 1,
                    Random.nextInt(rect.position.y + 1, rect.position.y + 1 + rect.size.h - 2)
                )
            }
        } while ((position.x == rect.position.x && position.y == rect.position.y)
            || (position.x == rect.position.x + rect.size.w - 1 && position.y == rect.position.y + rect.size.h - 1))

        return position
    }

    private fun findDirection(position: Position): Direction {
        return when {
            position.x == rect.position.x -> Direction.LEFT
            position.y == rect.position.y -> Direction.UP
            position.x == rect.position.x + rect.size.w - 1 -> Direction.RIGHT
            position.y == rect.position.y + rect.size.h - 1 -> Direction.DOWN
            else -> Direction.UP
        }
    }
}
