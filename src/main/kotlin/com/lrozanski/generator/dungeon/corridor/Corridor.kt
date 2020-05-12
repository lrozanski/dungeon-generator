package com.lrozanski.generator.dungeon.corridor

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.Grid
import com.lrozanski.generator.dungeon.data.Cell
import com.lrozanski.generator.dungeon.data.Position
import kotlin.math.max
import kotlin.random.Random

class Corridor(private val grid: Grid, startPosition: Position, private val maxLength: Int = 10) {

    val cells: MutableList<Position> = mutableListOf()

    lateinit var direction: Direction

    private var sectionLength: Int = 0

    init {
        cells += startPosition
    }

    fun head() = cells.last()
    fun tail() = cells[0]
    fun size() = cells.size

    fun step(previousDirection: Direction?): Direction? {
        val availableDirections = Direction.except(previousDirection?.opposite())
        val position = cells.last()

        while (availableDirections.isNotEmpty()) {
            val direction = weightedRandom(availableDirections, previousDirection, 0.8)
            val newPosition = position + direction.position

            val newDirections = Direction.except(direction.opposite())
            val adjacentPositions = newDirections
                .map { newPosition + it.position }
                .toList()

            val adjacentPositionsEmpty = adjacentPositions
                .all { grid[it]?.isEmpty() == true }

            if (grid.contains(newPosition.x, newPosition.y) && adjacentPositionsEmpty && canPlaceFloor(position, newPosition) && cells.size < maxLength) {
                cells += newPosition
                this.direction = direction
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

    fun placeFloor() = cells.forEach { position ->
        if (grid[position]!!.isEmpty()) {
            grid[position] = Cell(position, CellType.FLOOR)
        }
    }

    fun placeWalls() {
        cells.forEach { position ->
            position
                .adjacent()
                .filter { adjacentPosition -> grid.contains(adjacentPosition.x, adjacentPosition.y) }
                .filter { adjacentPosition -> !cells.contains(adjacentPosition) }
                .filter { adjacentPosition -> grid[adjacentPosition]!!.isEmpty() }
                .forEach { adjacentPosition -> grid[adjacentPosition] = Cell(adjacentPosition, CellType.WALL) }
        }
    }
}
