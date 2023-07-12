import kotlin.math.sqrt
import java.util.*
import javax.swing.JFrame
import kotlin.math.pow

fun distance(a: Pair<Int, Int>, b: Pair<Int, Int>): Double {
    return sqrt((a.first - b.first).toDouble().pow(2) + (a.second - b.second).toDouble().pow(2))
}


object Common {
//    fun moveWindow(handle: JFrame, x: Int, y: Int) {
//        handle.setLocation(x, y)
//    }
//
//    fun runIfEnabled(function: () -> Unit) {
//        if (config.enabled) {
//            function()
//        }
//    }
//
//    fun runIfDisabled(message: String = "", function: () -> Unit) {
//        if (!config.enabled) {
//            function()
//        } else if (message.isNotEmpty()) {
//            println(message)
//        }
//    }

//
//    fun moveWindow(handle: Long, x: Int, y: Int) {
//        // implementation for moving the window using the window handle in Kotlin
//    }
//
//    fun runIfEnabled(function: () -> Unit) {
//        if (config.enabled) {
//            function()
//        }
//    }
//
//    fun runIfDisabled(message: String = "", function: () -> Unit) {
//        if (!config.enabled) {
//            function()
//        } else if (message.isNotEmpty()) {
//            println(message)
//        }
//    }

//    fun distance(a: Pair<Int, Int>, b: Pair<Int, Int>): Double {
//        return sqrt((a.first - b.first).toDouble().pow(2) + (a.second - b.second).toDouble().pow(2))
//    }

//    fun gameWindowClick(point: Pair<Int, Int> = Pair(0, 0), button: String = "left", clickTime: Int = 1, delay: Double = 0.4) {
//        if (!config.enabled) {
//            return
//        }
//        val target = Pair(point.first + config.capture.window["left"], point.second + config.capture.window["top"])
//        click(target, button, clickTime)
//        Thread.sleep(randFloat(delay, delay * 1.5).toLong())
//    }

//    fun waitForIsStanding(ms: Int = 2000): Boolean {
//        for (i in 0 until ms / 10) {
//            if (config.playerStates["isStanding"] == true) {
//                return true
//            }
//            Thread.sleep(10)
//        }
//        return false
//    }
//
//    fun waitForIsJumping(ms: Int = 2000): Boolean {
//        for (i in 0 until ms / 10) {
//            if (config.playerStates["movementState"] == config.MOVEMENT_STATE_JUMPING) {
//                return true
//            }
//            Thread.sleep(10)
//        }
//        return false
//    }

    fun waitForIsFalling(ms: Int = 2000): Boolean {
        for (i in 0 until ms / 10) {
            if (config.playerStates["movementState"] == config.MOVEMENT_STATE_FALLING) {
                return true
            }
            Thread.sleep(10)
        }
        return false
    }

    fun checkIsJumping(): Boolean {
        return !config.playerStates["isStanding"]!!
    }

    fun getIfSkillReady(skill: String, bias: Int = 0): Boolean {
        val commandBook = config.bot.commandBook
        var targetSkillName: String? = null
        val skills = skill.split("|")
        for (key in commandBook.keys) {
            for (s in skills) {
                val skillAndBias = s.split('-')
                val skillName: String
                val skillBias: Float
                if (skillAndBias.size == 1) {
                    skillName = skillAndBias[0]
                    skillBias = 0f
                } else {
                    skillName = skillAndBias[0]
                    skillBias = skillAndBias[1].toFloat()
                }
                if (key.equals(skillName, ignoreCase = true)) {
                    targetSkillName = commandBook[key]?.name
                    if (commandBook[key]?.isSkillReady(bias.toFloat()) == true) {
                        break
                    } else {
                        targetSkillName = null
                    }
                }
            }
            if (targetSkillName != null) {
                break
            }
        }
        return targetSkillName != null
    }

    fun getIsInSkillBuff(skill: String): Boolean {
        val commandBook = config.bot.commandBook
        var targetSkillName: String? = null
        val skills = skill.split("|")
        for (key in commandBook.keys) {
            for (s in skills) {
                val skillAndBias = s.split('-')
                val skillName: String
                val skillBias: Float
                if (skillAndBias.contains("+")) {
                    skillAndBias[1] = skillAndBias[1].toFloat() * -1
                }
                if (skillAndBias.size == 1) {
                    skillName = skillAndBias[0]
                    skillBias = 0f
                } else {
                    skillName = skillAndBias[0]
                    skillBias = skillAndBias[1].toFloat()
                }
                if (key.equals(skillName, ignoreCase = true)) {
                    targetSkillName = commandBook[key]?.name
                    if (config.skillCdTimer[targetSkillName] == null) {
                        config.skillCdTimer[targetSkillName] = 0f
                    }
                    if ((System.currentTimeMillis() / 1000.0 + skillBias) - config.skillCdTimer[targetSkillName]!! < commandBook[targetSkillName.toLowerCase()].buffTime) {
                        break
                    } else {
                        targetSkillName = null
                    }
                }
            }
            if (targetSkillName != null) {
                break
            }
        }
        return targetSkillName != null
    }

    fun separateArgs(arguments: Array<String>): Pair<Array<String>, Map<String, String>> {
        val args = mutableListOf<String>()
        val kwargs = mutableMapOf<String, String>()
        for (a in arguments) {
            val arg = a.trim()
            val index = arg.indexOf('=')
            if (index > -1) {
                val key = arg.substring(0, index).trim()
                val value = arg.substring(index + 1).trim()
                kwargs[key] = value
            } else {
                args.add(arg)
            }
        }
        return Pair(args.toTypedArray(), kwargs)
    }

    fun singleMatch(frame: Mat, template: Mat): Pair<Point, Point> {
        val gray = Mat()
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)
        val result = Mat()
        Imgproc.matchTemplate(gray, template, result, Imgproc.TM_CCOEFF)
        val minMaxResult = Core.minMaxLoc(result)
        val top_left = minMaxResult.minLoc
        val w = template.cols()
        val h = template.rows()
        val bottom_right = Point(top_left.x + w, top_left.y + h)
        return Pair(top_left, bottom_right)
    }

    fun multiMatch(frame: Mat, template: Mat, threshold: Double = 0.95): List<Point> {
        val gray = Mat()
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)
        val result = Mat()
        Imgproc.matchTemplate(gray, template, result, Imgproc.TM_CCOEFF_NORMED)
        val locations = Core.findNonZero(result).toList()
        val results = mutableListOf<Point>()
        for (p in locations) {
            val x = (p.x + template.cols() / 2).toInt()
            val y = (p.y + template.rows() / 2).toInt()
            results.add(Point(x, y))
        }
        return results
    }

    fun singleMatchWithThreshold(frame: Mat, template: Mat, threshold: Double = 0.95): List<Point> {
        val gray = Mat()
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)
        val result = Mat()
        Imgproc.matchTemplate(gray, template, result, Imgproc.TM_CCOEFF_NORMED)
        val minMaxResult = Core.minMaxLoc(result)
        val score = minMaxResult.maxVal
        val top_left = minMaxResult.maxLoc
        val results = mutableListOf<Point>()
        if (score >= threshold) {
            val x = (top_left.x + template.cols() / 2).toInt()
            val y = (top_left.y + template.rows() / 2).toInt()
            results.add(Point(x, y))
        }
        return results
    }

    fun singleMatchWithDigit(frame: Mat, template: Mat, threshold: Double = 0.95): List<Point> {
        val gray = Mat()
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY)
        val result = Mat()
        Imgproc.matchTemplate(gray, template, result, Imgproc.TM_CCOEFF_NORMED)
        val minMaxResult = Core.minMaxLoc(result)
        val score = minMaxResult.maxVal
        val top_left = minMaxResult.maxLoc
        val results = mutableListOf<Point>()
        if (score >= threshold) {
            val x = (top_left.x + template.cols() / 2).roundToInt()
            val y = (top_left.y + template.rows() / 2).roundToInt()
            results.add(Point(x, y))
        }
        return results
    }

    fun convertToRoundInt(point: Point, frame: Mat): Pair<Int, Int> {
        val x = point.x.toInt()
        val y = point.y.toInt()
        return Pair(x, y)
    }

    fun convertToRelative(point: Point, frame: Mat): Pair<Int, Int> {
        val x = point.x.toInt()
        val y = point.y.toInt()
        return Pair(x, y)
    }

    fun convertToAbsolute(point: Pair<Int, Int>, frame: Mat): Pair<Int, Int> {
        return point
    }

    fun filterColor(img: Mat, ranges: List<Pair<Scalar, Scalar>>): Mat {
        val hsv = Mat()
        Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV)
        val mask = Mat()
        Core.inRange(hsv, ranges[0].first, ranges[0].second, mask)
        for (i in 1 until ranges.size) {
            Core.bitwise_or(mask, Mat().also { Core.inRange(hsv, ranges[i].first, ranges[i].second, it) }, mask)
        }
        val colorMask = Mat.zeros(img.size(), img.type())
        Core.bitwise_and(img, img, colorMask, mask)
        return colorMask
    }

    fun drawLocation(minimap: Mat, pos: Point, color: Scalar) {
        val center = convertToAbsolute(pos, minimap)
        val radius = if (settings.moveTolerance < 1) {
            (minimap.cols() * settings.moveTolerance).toInt()
        } else {
            settings.moveTolerance.toInt()
        }
        Imgproc.circle(minimap, center, radius, color, 1)
    }

    fun printSeparator() {
        println("\n\n")
    }

    fun printState() {
        printSeparator()
        println("#".repeat(18))
        println("#    ${if (config.enabled) "ENABLED" else "DISABLED"}    #")
        println("#".repeat(18))
    }

    fun closestPoint(points: List<Pair<Int, Int>>, target: Pair<Int, Int>): Pair<Int, Int>? {
        if (points.isNotEmpty()) {
            points.sortedBy { distance(it, target) }
            return points[0]
        }
        return null
    }

    fun bernoulli(p: Double): Boolean {
        return Random().nextDouble() < p
    }

    fun randFloat(start: Double, end: Double): Double {
        return (end - start) * Random().nextDouble() + start
    }
}

class Async(private val function: () -> Unit) : Thread() {
    private val queue = LinkedList<Any>()

    override fun run() {
        function()
        queue.add('x')
    }

    fun processQueue(root: JFrame) {
        if (queue.isEmpty()) {
            Timer(100) { processQueue(root) }.start()
        }
    }
}

fun asyncCallback(context: JFrame, function: () -> Unit) {
    val task = Async(function)
    task.start()
    Timer(100) { task.processQueue(context) }.start()
}