package core.images

import core.Config
import org.bytedeco.javacpp.DoublePointer
import org.bytedeco.javacpp.indexer.UByteRawIndexer
import org.bytedeco.javacv.Java2DFrameUtils
import org.bytedeco.opencv.global.opencv_core
import org.bytedeco.opencv.global.opencv_core.CV_32F
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.global.opencv_imgproc
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Point2f
import org.bytedeco.opencv.opencv_core.Scalar
import java.awt.image.BufferedImage
import kotlin.math.pow
import kotlin.math.sqrt

object ImageProcessor {


    fun invertImage(srcPath: String, destPath: String) {
        val sourceImage = opencv_imgcodecs.imread(srcPath, opencv_imgcodecs.IMREAD_COLOR)
        val destinationImage = Mat(sourceImage.rows(), sourceImage.cols(), sourceImage.type())
        opencv_core.bitwise_not(sourceImage, destinationImage)
        opencv_imgcodecs.imwrite(destPath, destinationImage)
    }

    fun convertToGrayscale(srcPath: String, destPath: String) {
        val sourceImage = opencv_imgcodecs.imread(srcPath, opencv_imgcodecs.IMREAD_COLOR)
        val destinationImage = Mat(sourceImage.rows(), sourceImage.cols(), org.bytedeco.opencv.global.opencv_core.CV_8UC1)
        opencv_imgproc.cvtColor(sourceImage, destinationImage, opencv_imgproc.CV_BGR2GRAY)
        opencv_imgcodecs.imwrite(destPath, destinationImage)
    }

    fun singleMatch(frame: Mat, template: Mat): Pair<Point, Point> {
        val gray = Mat()
        opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY)
        val result = Mat()
        opencv_imgproc.matchTemplate(gray, template, result, opencv_imgproc.TM_CCOEFF)

        val minVal = DoublePointer(1L)
        val maxVal = DoublePointer(1L)
        val pointMin = Point()
        val pointMax = Point()

        opencv_core.minMaxLoc(result, minVal, maxVal, pointMin, pointMax, null)

        val topLeft = pointMax
        val w = template.cols()
        val h = template.rows()
        val bottomRight = Point(topLeft.x() + w, topLeft.y() + h)

        return Pair(topLeft, bottomRight)
    }

    fun multiMatch(frame: Mat, template: Mat, threshold: Double = 0.95): List<Point> {
        val gray = Mat()
        opencv_imgproc.cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY)

        val result = Mat()
        opencv_imgproc.matchTemplate(gray, template, result, opencv_imgproc.TM_CCOEFF_NORMED)

        // Convert threshold values to Mat
        val lowerb = Mat(1, 1, CV_32F, Scalar(threshold))
        val upperb = Mat(1, 1, CV_32F, Scalar(1.0))

        val thresholded = Mat()
        opencv_core.inRange(result, lowerb, upperb, thresholded)

        val points = mutableListOf<Point>()
        val indexer: UByteRawIndexer = thresholded.createIndexer()

        for (i in 0 until thresholded.rows()) {
            for (j in 0 until thresholded.cols()) {
                if (indexer.get(i.toLong(), j.toLong()).toInt() > 0) {
                    val x = (j + template.cols() / 2).toDouble()
                    val y = (i + template.rows() / 2).toDouble()
                    points.add(Point(x.toInt(), y.toInt()))
                }
            }
        }
        return points
    }


    private fun convertToAbsolute(point: Point2f, frame: Mat): Point {
        return if (point.x() < 1 && point.y() < 1) {
            Point((point.x() * frame.cols()).toInt(), (point.y() * frame.rows()).toInt())
        } else {
            Point(point.x().toInt(), point.y().toInt())
        }
    }

    fun drawLocation(minimap: Mat, pos: Point2f, color: Scalar) {
        val center = convertToAbsolute(pos, minimap)
        val radius = if (Config.moveThreshold < 1) {
            (minimap.cols() * Config.moveThreshold).toInt()
        } else {
            Config.moveThreshold
        }
        opencv_imgproc.circle(minimap, center, radius, color, 1, 8, 0)
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

    fun convertBufferedImageToMat(bufferedImage: BufferedImage): Mat {
        return Java2DFrameUtils.toMat(bufferedImage)
    }

    fun saveMatToImage(mat: Mat, filePath: String) {
        opencv_imgcodecs.imwrite(filePath, mat)
    }

}