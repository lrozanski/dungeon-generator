package com.lrozanski.generator.dungeon.map.data

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.corridor.Direction

data class Connector(val position: Position, val direction: Direction) {
    var unused: Boolean = false

    fun forward(amount: Int) = position + direction.position * amount
}

class Room(val rect: GridRect<Cell>, private val maxConnectors: Int = 5) {

    var connectors: MutableList<Connector> = mutableListOf()

    private fun remainingConnectors() = maxConnectors - connectors.size

    fun addConnector(connector: Connector) {
        connectors.add(connector)
    }

    fun cleanUpConnectors() = connectors.removeAll { it.unused }

    fun placeFloor() = forEachCell {
        rect.grid[it.x, it.y] = Cell(it.x, it.y, CellType.FLOOR)
    }

    fun placeWalls() = forEachCell {
        val x = it.x
        val y = it.y

        if ((x == rect.xMin || x == rect.xMax) || (y == rect.yMin || y == rect.yMax)) {
            rect.grid[it.x, it.y] = Cell(it.x, it.y, CellType.WALL)
        }
    }

    fun placeConnectors() {
        val positions = mutableListOf<Position>()
        var tries = 0

        for (i in remainingConnectors() downTo 0) {
            var position: Position
            do {
                position = randomPointOnEdge()
            } while (positions.any { it.isAdjacent(position) } && tries++<1000)

            positions += position
        }
        connectors.addAll(positions.map { Connector(it, findDirection(it)) })
    }

    private fun findDirection(position: Position): Direction {
        return when {
            position.y == rect.yMin -> Direction.UP
            position.x == rect.xMax -> Direction.RIGHT
            position.y == rect.yMax -> Direction.DOWN
            position.x == rect.xMin -> Direction.LEFT
            else -> Direction.UP
        }
    }

    fun placeDoors() = connectors.forEach {
        rect.grid[it.position] = Cell(it.position, CellType.DOOR)
    }

    private fun forEachCell(action: (cell: Cell) -> Unit) = rect.grid.forEachCell(rect) { action(it) }

    private fun randomPointOnEdge(): Position = edge(Direction.values().random()).random()

    /**
     * Returns all positions on an edge, without corners.
     */
    fun edge(direction: Direction): List<Position> {
        return when (direction) {
            Direction.UP -> (rect.xMin + 1 until rect.xMax - 1).map { Position(it, rect.yMin) }.toList()
            Direction.RIGHT -> (rect.yMin + 1 until rect.yMax - 1).map { Position(rect.xMax, it) }.toList()
            Direction.DOWN -> (rect.xMin + 1 until rect.xMax - 1).map { Position(it, rect.yMax) }.toList()
            Direction.LEFT -> (rect.yMin + 1 until rect.yMax - 1).map { Position(rect.xMin, it) }.toList()
        }
    }
}
