package com.lrozanski.generator.dungeon.extensions

inline fun <reified T> arrayOf2d(columns: Int, rows: Int, initializer: (x: Int, y: Int) -> T): Array<Array<T>> = Array(columns) { x ->
    Array(rows) { y -> initializer(x, y) }
}
