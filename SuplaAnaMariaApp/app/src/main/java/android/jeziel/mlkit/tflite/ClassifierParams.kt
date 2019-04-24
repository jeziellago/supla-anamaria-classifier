package android.jeziel.mlkit.tflite


const val CLOUD_MODEL_NAME = "supla-anamaria"
const val LOCAL_MODEL_NAME = "supla-anamaria-local"
const val LOCAL_MODEL_ASSET = "supla_anamaria.tflite"
const val INPUT_SIZE = 224
const val MOBILE_NET_WIDTH = 224f
const val MOBILE_NET_HEIGHT = 224f
const val IMG_TYPE_RGB = 3
const val LABELS = 2

val CLASSIFIER_INPUT = intArrayOf(1, INPUT_SIZE, INPUT_SIZE, IMG_TYPE_RGB)
val CLASSIFIER_OUTPUT = intArrayOf(1, LABELS)

val TEST_IMAGES = arrayOf(
        R.drawable.s1,
        R.drawable.ana2,
        R.drawable.s3,
        R.drawable.ana3,
        R.drawable.ana4,
        R.drawable.s4
)
