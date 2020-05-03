package com.lrozanski.generator.dungeon.data

import com.lrozanski.generator.dungeon.Grid

data class GridRect(val grid: Grid, val position: Position, val size: Size) {

    fun area() = size.w * size.h

    fun grow(amount: Int = 1) = GridRect(grid, position.offset(-amount, -amount), size.add(amount * 2, amount * 2))
        .limitToBounds()

    fun isEmpty(): Boolean = grid.isRectangleEmpty(position, size)

    fun contains(x: Int, y: Int): Boolean {
        return x >= position.x && x <= position.x + size.w - 1
            && y >= position.y && y <= position.y + size.h - 1
    }

    fun limitToBounds(): GridRect {
        val newPosition = Position(
            when {
                position.x < 0 -> 0
                position.x >= grid.size.w -> grid.size.w - 1
                else -> position.x
            },
            when {
                position.y < 0 -> 0
                position.y >= grid.size.w -> grid.size.h - 1
                else -> position.y
            }
        )
        val newSize = Size(
            when {
                position.x + size.w >= grid.size.w -> grid.size.w - position.x
                else -> size.w
            },
            when {
                position.y + size.h >= grid.size.h -> grid.size.h - position.y
                else -> size.h
            }
        )

        return GridRect(grid, newPosition, newSize)
    }
}
