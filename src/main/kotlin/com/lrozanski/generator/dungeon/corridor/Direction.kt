package com.lrozanski.generator.dungeon.corridor

import com.lrozanski.generator.dungeon.map.data.Position

enum class Direction(val position: Position) {
    UP(Position(0, -1)),
    RIGHT(Position(1, 0)),
    DOWN(Position(0, 1)),
    LEFT(Position(-1, 0));

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            RIGHT -> LEFT
            DOWN -> UP
            LEFT -> RIGHT
        }
    }

    companion object {
        fun except(direction: Direction?): MutableList<Direction> = values()
            .filter { it != direction }
            .toMutableList()
    }
}
