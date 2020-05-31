package com.lrozanski.generator.dungeon.map

import com.lrozanski.generator.dungeon.map.data.GridRect
import com.lrozanski.generator.dungeon.map.data.Position
import com.lrozanski.generator.dungeon.map.data.Size

abstract class AbstractGrid<T>(val size: Size) {

    val grid: Array<Array<T>> = this.createGrid()

    abstract fun createGrid(): Array<Array<T>>

    operator fun get(x: Int, y: Int): T? = if (contains(x, y)) grid[x][y] else null
    operator fun get(position: Position): T? = get(position.x, position.y)

    open operator fun set(x: Int, y: Int, value: T) {
        if (contains(x, y)) {
            grid[x][y] = value
        }
    }

    operator fun set(position: Position, value: T) = set(position.x, position.y, value)

    abstract fun isRectangleEmpty(position: Position, size: Size): Boolean

    fun contains(x: Int, y: Int): Boolean {
        return x >= 0 && x <= grid.size - 1
            && y >= 0 && y <= grid[x].size - 1
    }

    fun isEdge(position: Position): Boolean {
        return position.x == 0 || position.x == grid.size - 1
            || position.y == 0 || position.y == grid.size - 1
    }

    fun allMatch(position: Position, size: Size, predicate: (cell: T) -> Boolean): Boolean {
        for (x in position.x..position.x + size.w) {
            for (y in position.y..position.y + size.h) {
                if (!predicate(this[x, y]!!)) {
                    return false
                }
            }
        }
        return true
    }

    fun anyMatch(position: Position, size: Size, predicate: (cell: T) -> Boolean): Boolean {
        for (x in position.x..position.x + size.w) {
            for (y in position.y..position.y + size.h) {
                if (predicate(this[x, y]!!)) {
                    return true
                }
            }
        }
        return false
    }

    fun forEachCell(action: (cell: T) -> Unit) = forEachCell(Position.zero, Size(size.w - 1, size.h - 1), action)
    fun forEachCell(gridRect: GridRect<T>, action: (cell: T) -> Unit) = forEachCell(gridRect.position, gridRect.size, action)

    private fun forEachCell(position: Position, size: Size, action: (cell: T) -> Unit) {
        for (x in position.x..position.x + size.w) {
            for (y in position.y..position.y + size.h) {
                action(this[x, y]!!)
            }
        }
    }

    fun filter(predicate: (cell: T) -> Boolean): List<T> {
        val cellList = mutableListOf<T>()

        forEachCell { cell -> cell.takeIf(predicate)?.apply { cellList.add(this) } }
        return cellList
    }

    open fun fill(initializer: (x: Int, y: Int) -> T) {
        for (x in 0 until size.w) {
            for (y in 0 until size.h) {
                grid[x][y] = initializer(x, y)
            }
        }
    }
}
