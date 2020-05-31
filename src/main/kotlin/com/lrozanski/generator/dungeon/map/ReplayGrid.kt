package com.lrozanski.generator.dungeon.map

import com.lrozanski.generator.dungeon.CellType
import com.lrozanski.generator.dungeon.extensions.arrayOf2d
import com.lrozanski.generator.dungeon.map.data.Cell
import com.lrozanski.generator.dungeon.map.data.Size
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File

class ReplayGrid(size: Size) : Grid(size) {

//    override fun createGrid(): Array<Array<Cell>> {
//        actions.clear()
//
//        val newActions = mutableListOf<Cell>()
//        val grid = arrayOf2d(size.w, size.h) { x, y ->
//            val cell = Cell(x, y, CellType.EMPTY)
//            newActions += cell
//            return@arrayOf2d cell
//        }
//        actions += newActions
//        return grid
//    }

    override fun set(x: Int, y: Int, value: Cell) {
        if (contains(x, y)) {
            grid[x][y] = value
            actions += mutableListOf(value)
        }
    }

//    override fun fill(initializer: (x: Int, y: Int) -> Cell) {
//        val newActions = mutableListOf<Cell>()
//        for (x in 0 until size.w) {
//            for (y in 0 until size.h) {
//                grid[x][y] = initializer(x, y)
//                newActions.add(grid[x][y])
//            }
//        }
//        actions += newActions
//    }

    fun writeReplay(file: File) {
        val json = Json(JsonConfiguration.Stable)
        val jsonData = json.stringify(Cell.serializer().list.list, actions)

        file.writeText(jsonData)
    }

    companion object {
        private val actions: MutableList<MutableList<Cell>> = mutableListOf()
    }
}
