package com.lrozanski.generator.dungeon.map.corridor

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.map.Grid
import com.lrozanski.generator.dungeon.map.Mask
import com.lrozanski.generator.dungeon.map.data.*
import com.lrozanski.generator.dungeon.visualization.debug
import java.awt.Color
import kotlin.math.max
import kotlin.random.Random

class Corridor(private val grid: Grid, startPosition: Position, private val fork: Boolean = false, private val maxLength: Int = 25) {

    val cells: MutableList<Position> = mutableListOf()

    lateinit var direction: Direction

    private var sectionLength: Int = 0

    init {
        cells += startPosition
    }

    //    fun head() = cells.last()
    fun head(previous: Int = 0) = cells[cells.size - previous - 1]
    fun tail() = cells[0]
    fun size() = cells.size

    fun step(corridorMask: Mask, previousDirection: Direction?, weight: Double): Direction? {
        val availableDirections = Direction.except(previousDirection?.opposite())
        val position = head()

        while (availableDirections.isNotEmpty()) {
            val direction = weightedRandom(availableDirections, previousDirection, weight)
            val newPosition = position + direction.position
            val maskRect: GridRect<Cell> = growPerpendicular(newPosition, direction, 2)
            val corridorMaskEmpty = corridorMask.isEmpty(maskRect) || direction != previousDirection

            if (grid.contains(newPosition.x, newPosition.y) && !grid.isEdge(newPosition) && cells.size < maxLength) {
                if (fork && grid[newPosition]!!.isEmpty()) {
//                    debug(newPosition, Color.MAGENTA)

                    cells += newPosition
                    this.direction = direction
                    corridorMask.add(newPosition)
                    return direction
                } else if (corridorMaskEmpty && maskRect.isEmpty()) {
                    cells += newPosition
                    this.direction = direction
                    corridorMask.add(newPosition)
                    return direction
                }
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

    fun growPerpendicular(position: Position, direction: Direction, size: Int): GridRect<Cell> {
        val horizontal = GridRect(grid, position - Position(size, 0), Size(size * 2, 0)).limitToBounds()
        val vertical = GridRect(grid, position - Position(0, size), Size(0, size * 2)).limitToBounds()

        return when (direction) {
            Direction.UP, Direction.DOWN -> horizontal
            Direction.RIGHT, Direction.LEFT -> vertical
        }
    }

    fun placeFloor(secret: Boolean = false) = cells.forEach { position ->
        if (grid[position]!!.isEmpty()) {
            grid[position] = Cell(position.x, position.y, CellType.FLOOR, secret)
        }
    }

    fun placeStairs(cellType: CellType) {
        var placed = false
        var tries = 0
        val canReplace = setOf(CellType.WALL, CellType.EMPTY)

        do {
            val cell = cells
                .random()
                .adjacent()
                .filter { grid[it]?.type in canReplace }
                .takeIf { it.isNotEmpty() }
                ?.random()

            cell?.apply {
                grid[this] = Cell(this, cellType)
                placed = true

                adjacent()
                    .filter { grid[it]?.isEmpty() == true }
                    .forEach { grid[it] = Cell(it, CellType.WALL) }
            }
        } while (!placed && tries++ < 100)
    }

    fun placeWalls(secret: Boolean = false) {
        cells.forEach { position ->
            position
                .adjacent()
                .filter { adjacentPosition -> grid.contains(adjacentPosition.x, adjacentPosition.y) }
                .filter { adjacentPosition -> !cells.contains(adjacentPosition) }
                .filter { adjacentPosition -> grid[adjacentPosition]!!.isEmpty() }
                .forEach { adjacentPosition -> grid[adjacentPosition] = Cell(adjacentPosition.x, adjacentPosition.y, CellType.WALL, secret) }
        }
    }

    fun fork(): Connector? {
        val cell = cells.random()
        val direction = findDirection(cell) ?: return null

        if (grid[cell + direction.position + direction.position]?.isEmpty() == true) {
            return Connector(cell + direction.position, direction)
        }
        return null
    }

    private fun findDirection(cell: Position): Direction? {
        return Direction
            .values()
            .filter { grid[cell + it.position]?.type == CellType.WALL }
            .takeIf { it.isNotEmpty() }
            ?.randomElementOrNull()
    }

    private fun <T> Collection<T>.randomElementOrNull(): T? {
        return if (this.isEmpty()) null else this.random()
    }
}
