package com.candra.latihanwidget

import java.util.*


// Ini adalah kelas helper
internal object NumberGenerator {
    fun gererate(max: Int): Int{
        val random = Random()
        return random.nextInt()
    }
}