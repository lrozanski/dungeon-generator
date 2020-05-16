package com.lrozanski.generator.dungeon.map

import com.lrozanski.generator.dungeon.corridor.Corridor
import com.lrozanski.generator.dungeon.extensions.arrayOf2d
import com.lrozanski.generator.dungeon.map.data.Cell
import com.lrozanski.generator.dungeon.map.data.GridRect
import com.lrozanski.generator.dungeon.map.data.Position
import com.lrozanski.generator.dungeon.map.data.Size

class Mask(size: Size) : AbstractGrid<Boolean>(size) {

    override fun createGrid(): Array<Array<Boolean>> = arrayOf2d(size.w, size.h) { _, _ -> EMPTY }

    override fun isRectangleEmpty(position: Position, size: Size): Boolean = super.allMatch(position, size) { it == EMPTY }
    fun isRectangleFilled(position: Position, size: Size): Boolean = anyMatch(position, size) { it == FILLED }

    fun isEmpty(position: Position) = grid[position.x][position.y] == EMPTY
    fun isFilled(position: Position) = grid[position.x][position.y] == FILLED

    fun isEmpty(rect: GridRect<Cell>) = rect.allCellsMatch { grid[it.x][it.y] == EMPTY }

    fun allMatch(position: Position, size: Size, predicate: (x: Int, y: Int, cell: Boolean) -> Boolean): Boolean {
        for (x in position.x..position.x + size.w) {
            for (y in position.y..position.y + size.h) {
                if (!predicate(x, y, this[x, y]!!)) {
                    return false
                }
            }
        }
        return true
    }

    fun forEachCell(action: (x: Int, y: Int, cell: Boolean) -> Unit) {
        for (x in 0 until size.w) {
            for (y in 0 until size.h) {
                action(x, y, this[x, y]!!)
            }
        }
    }

    fun copy(): Mask {
        val mask = Mask(size)
        grid.copyInto(mask.grid)
        return mask
    }

    fun add(position: Position) {
        if (contains(position.x, position.y)) {
            grid[position.x][position.y] = FILLED
        }
    }

    fun subtract(position: Position) {
        if (contains(position.x, position.y)) {
            grid[position.x][position.y] = EMPTY
        }
    }

    fun add(cell: Cell) {
        grid[cell.x][cell.y] = FILLED
    }

    fun subtract(cell: Cell) {
        grid[cell.x][cell.y] = EMPTY
    }

    fun add(corridor: Corridor) {
        corridor.cells.forEach { add(it) }
    }

    fun subtract(corridor: Corridor) {
        corridor.cells.forEach { subtract(it) }
    }

    fun add(rect: GridRect<Cell>) {
        rect.forEachCell { add(it) }
    }

    fun subtract(rect: GridRect<Cell>) {
        rect.forEachCell { subtract(it) }
    }

    companion object {
        const val EMPTY = false
        const val FILLED = true
    }
}
