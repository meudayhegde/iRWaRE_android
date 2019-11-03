package com.irware.remote.ui

import android.content.Context
import android.renderscript.Allocation
import android.renderscript.Element.U8_4
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.RenderScript
import android.graphics.Bitmap
import kotlin.math.roundToInt


object BlurBuilder {
    private val BITMAP_SCALE = 0.4f
    private val BLUR_RADIUS = 2.5f

    fun blur(context: Context, image: Bitmap): Bitmap {
        val width = (image.width * BITMAP_SCALE).roundToInt()
        val height = (image.height * BITMAP_SCALE).roundToInt()

        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        val rs = RenderScript.create(context)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(BLUR_RADIUS)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)

        return outputBitmap
    }
}