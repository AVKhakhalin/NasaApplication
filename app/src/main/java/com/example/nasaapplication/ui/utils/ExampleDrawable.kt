package com.example.nasaapplication.ui.utils

import android.graphics.*
import android.graphics.drawable.Drawable


class ExampleDrawable: Drawable() {

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPath: Path = Path()

    override fun draw(canvas: Canvas) {
        canvas.drawPath(mPath, mPaint)
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setAlpha(alpha: Int) {
        mPaint.setAlpha(alpha)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.setColorFilter(colorFilter)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        val width: Float = bounds.width() as Float
        val height: Float = bounds.height() as Float
        mPath.reset()
        mPath.moveTo(0f, height / 2)
        mPath.lineTo(width / 4, 0f)
        mPath.lineTo(width * 3 / 4, 0f)
        mPath.lineTo(width, height / 2)
        mPath.lineTo(width * 3 / 4, height)
        mPath.lineTo(width / 4, height)
        mPath.close()
    }
}