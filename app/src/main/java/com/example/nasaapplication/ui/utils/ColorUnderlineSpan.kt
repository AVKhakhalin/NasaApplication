package com.example.nasaapplication.ui.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import android.widget.Toast
import com.example.nasaapplication.R
import com.example.nasaapplication.ui.activities.MainActivity

class ColorUnderlineSpan(
    val mainActivity: MainActivity,
    val underlineColor: Int,
    val underlineStart: Int,
    val underlineEnd: Int
):
    LineBackgroundSpan {
    //region ЗАДАНИЕ ПЕРЕМЕННЫХ
    val paint = Paint()
    //endregion
    // Конструктор
    init {
        paint.color = underlineColor
        paint.strokeWidth = 7.0f
        paint.style = Paint.Style.FILL_AND_STROKE
    }
    // Метод отрисовки
    override fun drawBackground(
        c: Canvas, p: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
        text: CharSequence, start: Int, end: Int, lnum: Int) {
        if (underlineStart >= underlineEnd) {
            throw Error("${mainActivity.resources.getString(R.string.error)}: ${
                mainActivity.resources.getString(R.string.error_underline_creation_for_title)}")
        }

        if (underlineStart > end || underlineEnd < start) {
            return
        }

        var offsetX = 0

        if (underlineStart > start) {
            offsetX = p.measureText(text.subSequence(start, underlineStart).toString()).toInt()
        }

        val length: Int = p.measureText(
            text.subSequence(Math.max(start, underlineStart),
                Math.min(end, underlineEnd)).toString()).toInt()

        c.drawLine(offsetX.toFloat(), baseline + paint.strokeWidth,
            (length + offsetX).toFloat(),baseline + paint.strokeWidth, paint)
    }
}