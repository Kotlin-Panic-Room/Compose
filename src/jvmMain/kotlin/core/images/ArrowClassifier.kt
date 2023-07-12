//package core.images
//
//import org.tensorflow.*
//import org.tensorflow.types.UInt8
//import java.awt.image.BufferedImage
//import java.awt.image.DataBufferByte
//import java.awt.image.DataBufferUShort
//import java.nio.ByteBuffer
//
//
//class ArrowClassifier(private val modelDir: String) {
//    private val labelMap = mapOf(1 to "up", 2 to "down", 3 to "left", 4 to "right")
//    private val converter = mapOf("up" to "right", "down" to "left")
//
//    private lateinit var model: SavedModelBundle
//
//    fun loadModel() {
//        model = SavedModelBundle.load(modelDir)
//    }
//
//    fun runInferenceForSingleImage(model: Session, image: Mat): Map<String, Any> {
//        val tensor = Tensor.create(UInt8::class.java, longArrayOf(image.total(), 3L), image.nativeObjAddr)
//
//        val runner = model.runner()
//        runner.feed("image_tensor", tensor)
//        runner.fetch("detection_scores")
//        runner.fetch("detection_boxes")
//        runner.fetch("detection_classes")
//
//        val result = runner.run()
//        val scores = result[0].copyTo(Array(numDetections) { FloatArray(91) })
//        val boxes = result[1].copyTo(Array(numDetections) { FloatArray(4) })
//        val classes = result[2].copyTo(IntArray(numDetections))
//
//        return mapOf(
//            "detection_scores" to scores,
//            "detection_boxes" to boxes,
//            "detection_classes" to classes
//        )
//    }
//
//
//    fun classifyArrows(image: BufferedImage): List<String> {
//        val resizedImage = resizeImage(image)
//        val inputTensor = createInputTensor(resizedImage)
//        val outputTensor = runInference(inputTensor)
//        val predictions = processOutputTensor(outputTensor)
//
//        return mergeDetection(predictions)
//    }
//
//
//    private fun createInputTensor(image: BufferedImage): Tensor<Uint8> {
//        val byteBuffer = ByteBuffer.wrap((image.raster.dataBuffer as DataBufferByte).data)
//        val shape = Shape.make(1, 384, 455, 3)
//        return Tensor.create(UInt8::class.java, shape, byteBuffer)
//    }
//
//    private fun runInference(inputTensor: Tensor<Uint8>): Tensor<Float> {
//        return model.session().runner()
//            .feed("input_tensor", inputTensor)
//            .fetch("detection_scores")
//            .fetch("detection_boxes")
//            .fetch("detection_classes")
//            .run()[0].expect(Float::class.javaObjectType)
//    }
//
//
//    private fun rotateImage(image: BufferedImage): BufferedImage {
//        val rotatedImage = BufferedImage(384, 455, BufferedImage.TYPE_3BYTE_BGR)
//        val rotatedRaster = rotatedImage.raster
//        val originalRaster = image.raster
//
//        for (y in 0 until 384) {
//            for (x in 0 until 455) {
//                rotatedRaster.setPixel(y, x, originalRaster.getPixel(x, y, IntArray(3)))
//            }
//        }
//
//        return rotatedImage
//    }
//
//    private fun BufferedImage.toByteTensor(): ByteArray {
//        val raster = this.raster
//        val dataBuffer = raster.dataBuffer
//        return if (dataBuffer is DataBufferByte) {
//            dataBuffer.data
//        } else {
//            (dataBuffer as DataBufferUShort).data.map { it.toByte() }.toByteArray()
//        }
//    }
//}
