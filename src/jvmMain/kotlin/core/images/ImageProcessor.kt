package core.images

import core.Config
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ImageProcessor {

    init {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    }

    fun invertImage(srcPath: String, destPath: String) {
        val sourceImage = Imgcodecs.imread(srcPath, Imgcodecs.IMREAD_COLOR)
        val destinationImage = Mat(sourceImage.rows(), sourceImage.cols(), sourceImage.type())
        Core.bitwise_not(sourceImage, destinationImage)
        Imgcodecs.imwrite(destPath, destinationImage)
    }

    fun convertToGrayscale(srcPath: String, destPath: String) {
        val sourceImage = Imgcodecs.imread(srcPath, Imgcodecs.IMREAD_COLOR)
        val destinationImage = Mat(sourceImage.rows(), sourceImage.cols(), CvType.CV_8UC1)
        Imgproc.cvtColor(sourceImage, destinationImage, Imgproc.COLOR_BGR2GRAY)
        Imgcodecs.imwrite(destPath, destinationImage)
    }

    fun singleMatch(frame: Mat, template: Mat): Pair<Point, Point> {
        val gray = Mat()
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)
        val result = Mat()
        Imgproc.matchTemplate(gray, template, result, Imgproc.TM_CCOEFF)
        val minMaxResult = Core.minMaxLoc(result)
        val topLeft = minMaxResult.maxLoc
        val w = template.cols()
        val h = template.rows()
        val bottomRight = Point(topLeft.x + w, topLeft.y + h)
        return Pair(topLeft, bottomRight)
    }

    fun multiMatch(frame: Mat, template: Mat, threshold: Double = 0.95): List<Point> {
        val gray = Mat()
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)
        val result = Mat()
        Imgproc.matchTemplate(gray, template, result, Imgproc.TM_CCOEFF_NORMED)

        val thresHolded = Mat()
        Core.inRange(result, Scalar(threshold), Scalar(1.0), thresHolded)

        val points = mutableListOf<Point>()
        for (i in 0 until thresHolded.rows()) {
            for (j in 0 until thresHolded.cols()) {
                if (thresHolded.get(i, j)[0] > 0) {
                    val x = (j + template.cols() / 2).toDouble()
                    val y = (i + template.rows() / 2).toDouble()
                    points.add(Point(x, y))
                }
            }
        }

        return points
    }

    fun convertToRoundInt(point: Point): Point {
        return Point(point.x.roundToInt().toDouble(), point.y.roundToInt().toDouble())
    }

    fun convertToRelative(point: Point, frame: Mat): Point {
        return Point(point.x / frame.width(), point.y / frame.height())
    }

    fun convertToAbsolute(point: Point, frame: Mat): Point {
        return if (point.x < 1 && point.y < 1) {
            Point((point.x * frame.width()).roundToInt().toDouble(), (point.y * frame.height()).roundToInt().toDouble())
        } else {
            Point(point.x.roundToInt().toDouble(), point.y.roundToInt().toDouble())
        }
    }
    fun drawLocation(minimap: Mat, pos: Point, color: Scalar) {
        val center = convertToAbsolute(pos, minimap)
        val radius = if (Config.moveThreshold < 1) {
            (minimap.cols() * Config.moveThreshold).toInt()
        } else {
            Config.moveThreshold
        }
        Imgproc.circle(minimap, center, radius, color, 1)
    }

    private fun distance(a: Pair<Int, Int>, b: Pair<Int, Int>): Double {
        return sqrt((a.first - b.first).toDouble().pow(2) + (a.second - b.second).toDouble().pow(2))
    }

    fun closestPoint(points: List<java.awt.Point>, target: Pair<Int, Int>): java.awt.Point? {
        if (points.isNotEmpty()) {
            points.sortedBy { distance(Pair(it.x, it.y), target) }
            return points[0]
        }
        return null
    }

    companion object {
        fun matToByteArray(image: Mat): ByteArray {
            val matOfByte = MatOfByte()
            Imgcodecs.imencode(".jpg", image, matOfByte)
            return matOfByte.toArray()
        }
    }


}