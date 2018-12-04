package android.jeziel.mlkit.tflite

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jeziellago.android.imagekit.Image
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var currentPosition: Int = 0

    private val classifier: Classifier by lazy { Classifier() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNextButton()
    }

    private fun setupNextButton() {
        btn_next.setOnClickListener {
            if (currentPosition == TEST_IMAGES.size)
                currentPosition = 0

            val imgBmp = getImage(TEST_IMAGES[currentPosition++])
            iv_image.setImageBitmap(imgBmp)

            val resizedImage = Image(imgBmp)
                    .resize(MOBILE_NET_WIDTH, MOBILE_NET_HEIGHT)
                    .toBitmap()

            runClassification(resizedImage)
        }
        btn_next.performClick()
    }

    private fun runClassification(img: Bitmap) {
        val onSuccess = { result: Array<FloatArray> -> showClassification(result) }

        val onFailure = { e: Exception ->
            Log.e(MainActivity::class.java.simpleName, "onFailure", e)

            Toast.makeText(this@MainActivity,
                    "Error in classification",
                    Toast.LENGTH_SHORT)
                    .show()
        }
        classifier.run(img, onSuccess, onFailure)
    }

    private fun getImage(imgId: Int) = (resources.getDrawable(imgId, theme) as BitmapDrawable).bitmap

    private fun showClassification(output: Array<FloatArray>) {
        val v1 = output[0][0]
        val v2 = output[0][1]
        val top1: String
        val top2: String

        if (v1 > v2) {
            top1 = String.format("Ana Maria: %.4f", v1)
            top2 = String.format("Supla: %.4f", v2)
        } else {
            top1 = String.format("Supla: %.4f", v2)
            top2 = String.format("Ana Maria: %.4f", v1)
        }

        tv_top1.text = top1
        tv_top2.text = top2
    }
}
