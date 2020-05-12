package com.lrozanski.generator.dungeon.data

import com.lrozanski.generator.dungeon.Grid

data class GridRect(val grid: Grid, val position: Position, val size: Size) {

    val xMin = position.x
    val yMin = position.y
    val xMax = position.x + size.w
    val yMax = position.y + size.h

    fun area() = size.w * size.h

    fun grow(amount: Int = 1) = GridRect(grid, position.offset(-amount, -amount), size.add(amount * 2, amount * 2))
        .limitToBounds()

    fun offset(offset: Position) = GridRect(grid, position + offset, size)

    fun isEmpty(): Boolean = grid.isRectangleEmpty(position, size)

    fun isNotEmpty(): Boolean = !isEmpty()
    fun contains(x: Int, y: Int): Boolean {
        return x in xMin..xMax
            && y in yMin..yMax
    }

    fun isWithinBounds(): Boolean = xMin >= 0 && xMax < grid.size.w
        && yMin >= 0 && yMax < grid.size.h

    private fun limitToBounds(): GridRect {
        if (isWithinBounds()) {
            return this
        }
        val newPosition = Position(
            position.x.coerceIn(0, grid.size.w - 1),
            position.y.coerceIn(0, grid.size.h - 1)
        )
        val newSize = Size(
            when {
                xMax >= grid.size.w -> grid.size.w - position.x
                else -> size.w
            },
            when {
                yMax >= grid.size.h -> grid.size.h - position.y
                else -> size.h
            }
        )
        return GridRect(grid, newPosition, newSize)
    }

    fun outline(): List<Cell> {
        return grid.filter { cell ->
            (cell.x == xMin || cell.x == xMax) && (cell.y in yMin..yMax)
                || (cell.y == yMin || cell.y == yMax) && (cell.x in xMin..xMax)
        }
    }
}
