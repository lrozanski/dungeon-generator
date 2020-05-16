package com.lrozanski.generator.dungeon.map.data

import com.lrozanski.generator.dungeon.map.AbstractGrid

data class GridRect<T>(val grid: AbstractGrid<T>, val position: Position, val size: Size) {

    val xMin = position.x
    val yMin = position.y
    val xMax = position.x + size.w
    val yMax = position.y + size.h

    fun area() = size.w * size.h

    fun grow(amount: Int = 1) = GridRect(grid, position.offset(-amount, -amount), size.add(amount * 2 - 1, amount * 2 - 1))
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

    fun limitToBounds(): GridRect<T> {
        if (isWithinBounds()) {
            return this
        }
        val newPosition = Position(
            position.x.coerceIn(0, grid.size.w - 1),
            position.y.coerceIn(0, grid.size.h - 1)
        )
        val newSize = Size(
            when {
                xMax >= grid.size.w -> grid.size.w - position.x - 1
                else -> size.w
            },
            when {
                yMax >= grid.size.h -> grid.size.h - position.y - 1
                else -> size.h
            }
        )
        return GridRect(grid, newPosition, newSize)
    }

    fun forEachCell(action: (cell: T) -> Unit) {
        for (x in xMin..xMax) {
            for (y in yMin..yMax) {
                action(grid[x, y]!!)
            }
        }
    }

    fun allCellsMatch(action: (cell: T) -> Boolean): Boolean {
        for (x in xMin..xMax) {
            for (y in yMin..yMax) {
                if (!action(grid[x, y]!!)) {
                    return false
                }
            }
        }
        return true
    }
}
