package com.lrozanski.generator.dungeon.map

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.extensions.arrayOf2d
import com.lrozanski.generator.dungeon.map.data.Cell
import com.lrozanski.generator.dungeon.map.data.GridRect
import com.lrozanski.generator.dungeon.map.data.Position
import com.lrozanski.generator.dungeon.map.data.Size
import kotlin.math.max
import kotlin.math.min

open class Grid(size: Size) : AbstractGrid<Cell>(size) {

    override fun createGrid(): Array<Array<Cell>> = arrayOf2d(size.w, size.h) { x, y -> Cell(x, y, CellType.EMPTY) }

    override fun isRectangleEmpty(position: Position, size: Size): Boolean = allMatch(position, size) { it.type == CellType.EMPTY }

    operator fun get(position: Position, size: Size): GridRect<Cell> = GridRect(this, position, size)

    fun shrink(margin: Int = 1): Grid {
        var minX = size.w
        var minY = size.h
        var maxX = 0
        var maxY = 0

        for (x in 0 until size.w) {
            for (y in 0 until size.h) {
                val cell = grid[x][y]
                if (cell.isEmpty()) {
                    continue
                }
                minX = min(cell.x, minX)
                minY = min(cell.y, minY)
                maxX = max(cell.x, maxX)
                maxY = max(cell.y, maxY)
            }
        }

        val newGrid = Grid(Size(maxX - minX + 1 + margin * 2, maxY - minY + 1 + margin * 2))
//        newGrid.fill { x, y -> Cell(x, y, CellType.EMPTY) }

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                val newX = x - minX + 1
                val newY = y - minY + 1

                newGrid[newX, newY] = Cell(newX, newY, grid[x][y].type, grid[x][y].secret)
            }
        }
        return newGrid
    }
}
