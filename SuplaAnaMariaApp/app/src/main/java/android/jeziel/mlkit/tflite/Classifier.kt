package android.jeziel.mlkit.tflite

import android.graphics.Bitmap
import com.google.firebase.ml.common.modeldownload.FirebaseCloudModelSource
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModelSource
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.custom.*
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

        // configure local model .tflite
        val localSource = FirebaseLocalModelSource.Builder(LOCAL_MODEL_NAME)
                .setAssetFilePath(LOCAL_MODEL_ASSET)
                .build()

        // define download conditions to cloud model
        val conditionsBuilder = FirebaseModelDownloadConditions.Builder().requireWifi()
        val conditions = conditionsBuilder.build()
        val cloudSource = FirebaseCloudModelSource.Builder(CLOUD_MODEL_NAME)
                .enableModelUpdates(true)
                .setInitialDownloadConditions(conditions)
                .setUpdatesDownloadConditions(conditions)
                .build()

        // register local and cloud model
        with(FirebaseModelManager.getInstance()) {
            registerLocalModelSource(localSource)
            registerCloudModelSource(cloudSource)
        }

        val options = FirebaseModelOptions.Builder()
                .setCloudModelName(CLOUD_MODEL_NAME)
                .setLocalModelName(LOCAL_MODEL_NAME)
                .build()

        firebaseInterpreter = FirebaseModelInterpreter.getInstance(options)
    }
}