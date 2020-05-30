package com.example.breathingexerciseview

import android.content.res.Resources


fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Float.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Float.toPx() = this * Resources.getSystem().displayMetrics.density
fun Int.toPx() = this * Resources.getSystem().displayMetrics.density


fun pxToDp(px:Int)  = (px / Resources.getSystem().displayMetrics.density).toInt()

fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()