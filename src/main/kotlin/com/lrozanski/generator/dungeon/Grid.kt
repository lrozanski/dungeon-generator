package com.lrozanski.generator.dungeon

import com.lrozanski.generator.dungeon.data.*
import com.lrozanski.generator.dungeon.extensions.arrayOf2d

class Grid(val size: Size) {

    private val grid: Array<Array<Cell>> = arrayOf2d(size.w, size.h)

    operator fun get(x: Int, y: Int): Cell? = if (contains(x, y)) grid[x][y] else null

    operator fun get(position: Position): Cell? = get(position.x, position.y)
    operator fun get(position: Position, size: Size): GridRect = GridRect(this, position, size)

    operator fun set(x: Int, y: Int, value: Cell) {
        grid[x][y] = value
    }

    operator fun set(position: Position, value: Cell) = set(position.x, position.y, value)

    fun contains(x: Int, y: Int): Boolean {
        return x >= 0 && x <= grid.size - 1
            && y >= 0 && y <= grid[x].size - 1
    }

    fun isRectangleEmpty(position: Position, size: Size): Boolean = allMatch(position, size) { it.type == CellType.EMPTY }

    fun allMatch(position: Position, size: Size, predicate: (cell: Cell) -> Boolean): Boolean {
        for (x in position.x until position.x + size.w) {
            for (y in position.y until position.y + size.h) {
                if (!predicate(this[x, y]!!)) {
                    return false
                }
            }
        }
        return true
    }

    fun forEachCell(room: Room, action: (cell: Cell) -> Unit) = forEachCell(room.rect, action)
    fun forEachCell(action: (cell: Cell) -> Unit) = forEachCell(Position.zero, Size(size.w - 1, size.h - 1), action)
    fun forEachCell(gridRect: GridRect, action: (cell: Cell) -> Unit) = forEachCell(gridRect.position, gridRect.size, action)

    private fun forEachCell(position: Position, size: Size, action: (cell: Cell) -> Unit) {
        for (x in position.x..position.x + size.w) {
            for (y in position.y..position.y + size.h) {
                action(this[x, y]!!)
            }
        }
    }

    fun filter(predicate: (cell: Cell) -> Boolean): List<Cell> {
        val cellList = mutableListOf<Cell>()

        forEachCell { cell -> cell.takeIf(predicate)?.apply { cellList.add(this) } }
        return cellList
    }

    fun clear() {
        for (x in 0 until size.w) {
            for (y in 0 until size.h) {
                grid[x][y] = Cell(x, y, CellType.EMPTY)
            }
        }
    }
}
