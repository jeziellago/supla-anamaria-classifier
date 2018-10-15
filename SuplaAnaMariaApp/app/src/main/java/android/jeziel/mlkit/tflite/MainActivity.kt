package android.jeziel.mlkit.tflite

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var currentPosition: Int = 0

    private val classifier: Classifier by lazy { Classifier() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNextButton()
    }

    private fun setupNextButton() {
        btn_next.setOnClickListener { _ ->
            if (currentPosition == TEST_IMAGES.size)
                currentPosition = 0

            val imgBmp = getImage(TEST_IMAGES[currentPosition++])
            iv_image.setImageBitmap(imgBmp)
            runClassification(imgBmp.resizedToMobileNet())
        }
        btn_next.performClick()
    }

    private fun runClassification(img: Bitmap) {
        val onSuccess = { result: Array<FloatArray> ->
            showClassification(result)
        }

        val onFailure = { e: Exception ->
            Log.e(MainActivity::class.java.simpleName, "onFailure", e)

            Toast.makeText(this@MainActivity,
                    "Error in classification",
                    Toast.LENGTH_SHORT)
                    .show()
        }
        classifier.run(img, onSuccess, onFailure)
    }

    private fun getImage(imgId: Int): Bitmap {
        return (resources.getDrawable(imgId, theme) as BitmapDrawable).bitmap
    }

    private fun showClassification(output: Array<FloatArray>) {
        val v1 = output[0][0] * 100
        val v2 = output[0][1] * 100
        val classification = if (v1 > v2)
            "$v1% - Ana Maria\n $v2% - Supla"
        else {
            "$v2% - Supla\n$v1% - Ana Maria"
        }
        tv_classification.text = classification
    }
}
