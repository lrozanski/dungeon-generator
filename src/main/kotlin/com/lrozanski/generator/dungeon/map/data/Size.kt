package com.lrozanski.generator.dungeon.map.data

import kotlin.random.Random

data class Size(val w: Int, val h: Int) {

    fun add(w: Int, h: Int) = Size(this.w + w, this.h + h)

    companion object {
        val ONE = Size(1, 1)

        fun random(wMin: Int, wMax: Int, hMin: Int, hMax: Int) = Size(
            Random.nextInt(wMin, wMax),
            Random.nextInt(hMin, hMax)
        )
    }
}
