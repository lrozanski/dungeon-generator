package com.lrozanski.generator.dungeon.data

import kotlin.random.Random

data class Size(val w: Int, val h: Int) {

    fun add(w: Int, h: Int) = Size(this.w + w, this.h + h)

    companion object {
        fun random(wMin: Int, wMax: Int, hMin: Int, hMax: Int) = Size(
            Random.nextInt(wMin, wMax),
            Random.nextInt(hMin, hMax)
        )
    }
}
