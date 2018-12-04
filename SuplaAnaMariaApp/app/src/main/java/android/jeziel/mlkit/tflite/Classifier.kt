package android.jeziel.mlkit.tflite

import android.graphics.Bitmap
import com.google.firebase.ml.custom.*
import com.google.firebase.ml.custom.model.FirebaseCloudModelSource
import com.google.firebase.ml.custom.model.FirebaseModelDownloadConditions
import com.jeziellago.android.imagekit.Image
import java.lang.Exception

class Classifier {

    private var firebaseInterpreter: FirebaseModelInterpreter? = null

    private val inputOutputOptions: FirebaseModelInputOutputOptions by lazy {
        FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.FLOAT32, CLASSIFIER_INPUT)
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, CLASSIFIER_OUTPUT)
            .build()
    }

    init { initFirebaseModelInterpreter() }

    fun run(img: Bitmap, onSuccess:(Array<FloatArray>)-> Unit,
            onFailure:(Exception)-> Unit) {

        firebaseInterpreter?.let { interpreter ->
            interpreter.run(getFirebaseModelInputs(img), inputOutputOptions)
                .addOnSuccessListener { onSuccess(it.getOutput(0) as Array<FloatArray>) }
                .addOnFailureListener { onFailure(it) }
        }
    }

    private fun getFirebaseModelInputs(img: Bitmap): FirebaseModelInputs {
        val imgBuffer = Image(img).reshapeTo4D()
        return FirebaseModelInputs.Builder()
                .add(imgBuffer)
                .build()
    }

    private fun initFirebaseModelInterpreter() {
        val conditionsBuilder = FirebaseModelDownloadConditions.Builder().requireWifi()
        val conditions = conditionsBuilder.build()
        val cloudSource = FirebaseCloudModelSource.Builder(CLOUD_MODEL_NAME)
                .enableModelUpdates(true)
                .setInitialDownloadConditions(conditions)
                .setUpdatesDownloadConditions(conditions)
                .build()

        FirebaseModelManager.getInstance().registerCloudModelSource(cloudSource)

        val options = FirebaseModelOptions.Builder()
                .setCloudModelName(CLOUD_MODEL_NAME)
                .build()

        firebaseInterpreter = FirebaseModelInterpreter.getInstance(options)
    }
}