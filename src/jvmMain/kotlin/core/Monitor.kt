package core

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import core.Capture.getScreenShot
import core.Constants.MINIMAP_BOTTOM_BORDER
import core.Constants.MINIMAP_TOP_BORDER
import core.Constants.MMT_HEIGHT
import core.Constants.MMT_WIDTH
import core.Constants.MM_BR_TEMPLATE
import core.Constants.MM_TL_TEMPLATE
import core.Constants.PLAYER_TEMPLATE
import core.Constants.PT_HEIGHT
import core.Constants.PT_WIDTH
import core.images.ImageProcessor
import core.images.ImageProcessor.singleMatch
import core.player.Player
import core.player.State
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.opencv_core.Mat
import org.bytedeco.opencv.opencv_core.Point
import org.bytedeco.opencv.opencv_core.Rect
import kotlin.concurrent.thread
import kotlin.math.abs

object Constants {
    const val MINIMAP_TOP_BORDER = 5
    const val MINIMAP_BOTTOM_BORDER = 9
    const val WINDOWED_OFFSET_TOP = 36
    const val WINDOWED_OFFSET_LEFT = 10
    val MM_TL_TEMPLATE: Mat = opencv_imgcodecs.imread("D:\\project\\compose\\src\\jvmMain\\resources\\assets\\minimap_tl_template.png", opencv_imgcodecs.IMREAD_GRAYSCALE)
    val MM_BR_TEMPLATE: Mat = opencv_imgcodecs.imread("D:\\project\\compose\\src\\jvmMain\\resources\\assets\\minimap_br_template.png", opencv_imgcodecs.IMREAD_GRAYSCALE)
    val MMT_HEIGHT: Int = maxOf(MM_TL_TEMPLATE.rows(), MM_BR_TEMPLATE.rows())
    val MMT_WIDTH: Int = maxOf(MM_TL_TEMPLATE.cols(), MM_BR_TEMPLATE.cols())
    val PLAYER_TEMPLATE: Mat = opencv_imgcodecs.imread("D:\\project\\compose\\src\\jvmMain\\resources\\assets\\player_template.png", opencv_imgcodecs.IMREAD_GRAYSCALE)
    val PT_HEIGHT: Int = PLAYER_TEMPLATE.rows()
    val PT_WIDTH: Int = PLAYER_TEMPLATE.cols()
    const val SRCCOPY = 0x00CC0020
}

class Monitor {

    data class MiniMap(val ratio: Any?, val sample: Any?, val mat: Any?)

    private val user32: User32 = User32.INSTANCE
    private val captureGapSec: Double = 0.015
    private var minimap: Mat? = null
    private var minimapRatio: Double = 1.0
    private var minimapSample: Mat? = null
    var window = mutableMapOf<Any?, Int>()
    val defaultWindowResolution = mapOf(
        "1366" to Pair(1366, 768),
        "1280" to Pair(1280, 720)
    )
    var miniMap = MiniMap(null, null, null)
    var frame: Mat? = null
    var calibrated = false
    var refreshCounting = 0
    var handle :WinDef.HWND? = null
    var lastPlayerPos = Pair(-1f, -1f)

    init {
        thread(start = true) { main() }
    }


    fun main() {
        while (true) {
            // Calibrate screen capture
            handle = user32.FindWindow(null, "MapleStory") ?: continue

            assert(handle != null)

            val rect = getRect(handle!!)

//            if(settings.fullScreen) {
//                window["left"] = 0
//                window["top"] = 0
//                window["width"] = defaultWindowResolution["1366"]?.first
//                window["height"] = defaultWindowResolution["1366"]?.second
//            } else {
            window["left"] = rect.left
            window["top"] = rect.top
            window["width"] = maxOf(rect.right - rect.left, MMT_WIDTH)
            window["height"] = maxOf(rect.bottom - rect.top, MMT_HEIGHT)
//            }

            if (abs(defaultWindowResolution["1366"]?.first!! - window["width"] as Int) <
                abs(defaultWindowResolution["1280"]?.first!! - window["width"] as Int)
            ) {
                // window["left"] = rect.left + (defaultWindowResolution["1366"]?.first!! - window["width"] as Int)
                window["top"] = rect.top + abs(defaultWindowResolution["1366"]?.second!! - (window["height"] as Int))
                window["width"] = defaultWindowResolution["1366"]?.first!!.toInt()
                window["height"] = defaultWindowResolution["1366"]?.second!!.toInt()
            } else {
                // window["left"] = rect.left + (defaultWindowResolution["1280"]?.first!! - window["width"] as Int)
                window["top"] = rect.top + abs(defaultWindowResolution["1280"]?.second!! - (window["height"] as Int))
                window["width"] = defaultWindowResolution["1280"]?.first!!.toInt()
                window["height"] = defaultWindowResolution["1280"]?.second!!.toInt()
            }

            frame = getScreenShot(handle!!, 0, 0, window["width"] as Int, window["height"] as Int)



            val (tl, _) = singleMatch(frame!!, MM_TL_TEMPLATE)
            val (_, br) = singleMatch(frame!!, MM_BR_TEMPLATE)

            val mmTl = Point(tl.x() + MINIMAP_BOTTOM_BORDER, tl.y() + MINIMAP_TOP_BORDER)
            val mmBr = Point(
                maxOf(mmTl.x() + PT_WIDTH, br.x() - MINIMAP_BOTTOM_BORDER),
                maxOf(mmTl.y() + PT_HEIGHT, br.y() - MINIMAP_BOTTOM_BORDER)
            )

            val minimapRatio = (mmBr.x() - mmTl.x()) / (mmBr.y() - mmTl.y())
            val sampleRect = Rect(mmTl, mmBr)
            val minimapSample = Mat(frame, sampleRect).clone()
            miniMap = MiniMap(minimapRatio, minimapSample, null)
            calibrated = true

            while (Config.enabled){
                monitorPlayer(mmTl, mmBr)
                Thread.sleep((captureGapSec * 1000).toLong())
            }

        }
    }

    private fun monitorPlayer(mmTl: Point, mmBr: Point){
        if (!this.calibrated) {
            this.refreshCounting = 0
            return
        }
        // refresh whole game frame every 0.5s
        if (this.refreshCounting % 3 == 0) {
            this.frame = getScreenShot(this.handle!!,0,0,this.window["width"]!!,this.window["height"]!!)
        }


        // Take screenshot
        val minimap = getScreenShot(this.handle!! ,mmTl[0],mmTl[1],mmBr[0]-mmTl[0],mmBr[1]-mmTl[1])
        ImageProcessor.saveMatToImage(minimap, "D:\\project\\compose\\src\\jvmMain\\resources\\minimap.png")
        // Determine the player's position
        val player = ImageProcessor.multiMatch(minimap, PLAYER_TEMPLATE, threshold=0.8)
        println("player X: ${player.first().x()} Y: ${player.first().y()}")

        if (lastPlayerPos.second == player.first().y().toFloat()) {
            Player.state = State.STANDING
        }
        if (lastPlayerPos.second < player.first().y()) {
            Player.state = State.JUMPING
        }
        if (lastPlayerPos.second > player.first().y()) {
            Player.state = State.FALLING
        }


        // find left half or right half if didn't find complete player_template
        var findLeftHalf = false
        var findRightHalf = false
        var findBottomHalf = false
        // check is_standing
        lastPlayerPos = Player.position
        Player.position = Pair(player.first().x().toFloat(), player.first().y().toFloat())
//        if (findLeftHalf) {
//            Player.position = Pair(Player.position.first+2,Player.position.second)
//        }
//        if (findRightHalf) {
//            Player.position = Pair(Player.position.first-2,Player.position.second)
//        }
//        if (findBottomHalf) {
//            Player.position = Pair(Player.position.first,Player.position.second-1)
//        }
//        var doneCheckIsStanding = false

        // record if latest position has been changed
//            if (lastPlayerPos != Player.position || this.refreshCounting % 5 == 0) {
//                this.latestPositions.add(Player.position)
//                if (this.latestPositions.size > this.MAX_LATEST_POSITION_AMOUNT) {
//                    this.latestPositions.removeAt(0)
//                }
//            }


        // Your logic about settings.platforms and check_is_standing...
        // Rest of your code...

    }

    fun getRect(hWnd: WinDef.HWND): WinDef.RECT {
        val rect = WinDef.RECT()
        user32.GetWindowRect(hWnd, rect)
        rect.left = maxOf(0, rect.left)
        rect.top = maxOf(0, rect.top)
        rect.right = maxOf(0, rect.right)
        rect.bottom = maxOf(0, rect.bottom)
        return rect
    }
}