package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.*
import kotlin.math.max
import kotlin.random.Random

enum class Direction(val position: Position) {
    UP(Position(0, -1)),
    RIGHT(Position(1, 0)),
    DOWN(Position(0, 1)),
    LEFT(Position(-1, 0));

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }

    companion object {
        fun except(direction: Direction?) = values()
            .filter { it != direction }
            .toMutableList()
    }
}

data class Corridor(val grid: Grid, val startPosition: Position, val maxLength: Int = 15) {

    val cells: MutableList<Position> = mutableListOf()

    private var sectionLength: Int = 0

    init {
        cells += startPosition
    }

    fun step(room: Room, previousDirection: Direction?): Direction? {
        val availableDirections = Direction.except(previousDirection?.opposite())
        val position = cells.last()

        while (availableDirections.isNotEmpty()) {
            val direction = weightedRandom(availableDirections, previousDirection, 0.9)
            val newPosition = position + direction.position

            val newDirections = Direction.except(direction.opposite())
            val adjacentPositions = newDirections
                .map { newPosition + it.position }
                .toList()

            val adjacentPositionsEmpty = adjacentPositions
                .all { grid[it]?.isEmpty() == true }

            if (grid.contains(newPosition.x, newPosition.y) && adjacentPositionsEmpty && canPlaceFloor(position, newPosition) && cells.size < maxLength) {
                cells += newPosition
                return direction
            }
            availableDirections -= direction
        }
        return null
    }

    @Suppress("SameParameterValue")
    private fun weightedRandom(availableDirections: MutableList<Direction>, previousDirection: Direction?, weight: Double): Direction {
        val newWeight = max(0.0, weight - 0.1 * sectionLength)

        if (previousDirection == null) {
            return availableDirections.random()
        }
        return when {
            Random.nextFloat() <= newWeight -> previousDirection
            else -> availableDirections.random()
        }
    }

    private fun canPlaceFloor(previousPosition: Position, position: Position): Boolean = cells.none { it != previousPosition && it.isAdjacent(position) }

    fun placeWalls() {
        cells.forEach { position ->
            position
                .adjacent()
                .filter { adjacent -> grid.contains(adjacent.x, adjacent.y) }
                .filter { adjacent -> !cells.contains(adjacent) }
                .filter { adjacent -> grid[adjacent.x, adjacent.y]!!.isEmpty() }
                .forEach { adjacent -> grid[adjacent.x, adjacent.y] = Cell(adjacent.x, adjacent.y, CellType.WALL) }
        }
    }
}

class CorridorWalker {

    fun walk(grid: Grid, room: Room, connector: Connector): Boolean {
        var previousDirection: Direction? = connector.direction
        val corridor = Corridor(grid, connector.position)
        var i = 0

        do {
            previousDirection = corridor.step(room, previousDirection)
        } while (previousDirection != null && i++ < 1000)

        if (corridor.cells.size <= 1) {
            return false
        }
        val center = corridor.cells.last()
        val newRoomPosition = Position.random(center.x - 5, center.x, center.y - 5, center.y)
        val newRoomSize = Size.random(6, 13, 6, 13)
        val newRoomRect = GridRect(grid, newRoomPosition, newRoomSize).limitToBounds()
        val newRoom = RoomGenerator().createRoom(newRoomRect, 1) ?: return false

        corridor.cells
            .filter { position -> grid[position]?.type in setOf(CellType.EMPTY, CellType.WALL) }
            .forEach { position ->
                grid[position.x, position.y] = Cell(position.x, position.y, CellType.FLOOR)
            }
        corridor.placeWalls()

        newRoom.connectors.forEach {
            if (walk(grid, newRoom, it)) {
                grid[it.position.x, it.position.y] = Cell(it.position.x, it.position.y, CellType.DOOR)
            }
        }
        return true
    }
}
