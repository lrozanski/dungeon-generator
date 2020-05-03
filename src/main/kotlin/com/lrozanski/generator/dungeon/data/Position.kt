package com.lrozanski.generator.dungeon.data

import kotlin.math.abs
import kotlin.random.Random

data class Position(val x: Int, val y: Int) {

    fun offset(x: Int, y: Int) = Position(this.x + x, this.y + y)

    fun isAdjacent(other: Position): Boolean {
        return (abs(x - other.x) <= 1 && y == other.y)
            || (x == other.x && abs(y - other.y) <= 1)
    }

    fun adjacent(): List<Position> {
        return listOf(
            Position(x - 1, y - 1),
            Position(x, y - 1),
            Position(x + 1, y - 1),
            Position(x - 1, y),
            // center
            Position(x + 1, y),
            Position(x - 1, y + 1),
            Position(x, y + 1),
            Position(x + 1, y + 1)
        )
    }

    operator fun plus(other: Position): Position {
        return Position(x + other.x, y + other.y)
    }

    companion object {
        val zero = Position(0, 0)

        fun random(xMin: Int, xMax: Int, yMin: Int, yMax: Int) = Position(
            Random.nextInt(xMin, xMax),
            Random.nextInt(yMin, yMax)
        )
    }
}
