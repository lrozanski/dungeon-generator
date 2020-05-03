package com.lrozanski.generator.dungeon.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lrozanski.generator.dungeon.CellType

data class Cell(val x: Int, val y: Int, val type: CellType) {
    @JsonIgnore
    fun isEmpty() = type == CellType.EMPTY

    @JsonIgnore
    fun isNotEmpty() = !isEmpty()
}
