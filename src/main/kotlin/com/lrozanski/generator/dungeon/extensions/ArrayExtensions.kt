package com.lrozanski.generator.dungeon.extensions

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.data.Cell

fun arrayOf2d(columns: Int, rows: Int) = Array(columns) { x ->
    Array(rows) { y -> Cell(x, y, CellType.EMPTY) }
}
