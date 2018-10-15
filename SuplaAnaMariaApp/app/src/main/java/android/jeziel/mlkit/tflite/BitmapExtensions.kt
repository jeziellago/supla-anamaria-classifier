package android.jeziel.mlkit.tflite

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.resizedToMobileNet(): Bitmap {
    val width = width
    val height = height
    val scaleWidth = 224.toFloat() / width
    val scaleHeight = 224.toFloat() / height
    val matrix = Matrix()

    matrix.postScale(scaleWidth, scaleHeight)

    return Bitmap.createBitmap(this, 0, 0, width, height,
            matrix, false)
}

fun Bitmap.convertBitmapToByteBuffer(inputSize: Int): Array<Array<Array<FloatArray>>> {
    val intValues = IntArray(inputSize * inputSize)
    getPixels(intValues, 0, width, 0, 0, width, height)
    var pixel = 0

    val final = Array(1){ _ -> Array(inputSize){ _ -> Array(inputSize){FloatArray(3)}}}
    for (i in 0 until inputSize) {
        for (j in 0 until inputSize) {
            val px = intValues[pixel++]
            val r = ((((px shr 16 and 0xFF)/ 255.0f - 0.5f) * 2))
            val g = ((((px shr 8 and 0xFF)/ 255.0f - 0.5f) * 2))
            val b = ((((px and 0xFF)/ 255.0f - 0.5f) * 2))
            final[0][i][j][0] = r
            final[0][i][j][1] = g
            final[0][i][j][2] = b
        }
    }
    return final

}