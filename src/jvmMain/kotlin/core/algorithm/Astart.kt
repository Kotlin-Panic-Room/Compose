package core.algorithm

import core.Config
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

data class Node(val x: Float, val y: Float, var upLeft: Node? = null, var upRight: Node? = null, var downLeft: Node? = null, var downRight: Node? = null)

object Utils {
    fun closestPoint(points: List<Node>, target: Node): Node {
        return points.minByOrNull { distance(it, target) } ?: target
    }

    fun distance(a: Node, b: Node): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }
}

class AStar {

    private var root: Node? = null
    private val tolerance = Config.moveThreshold / 2
    fun shortestPath(source: Node, target: Node): List<Node> {
        val fringe = PriorityQueue<Pair<Float, Int>>(compareBy { it.first })
        val vertices = ArrayList<Node>().apply { add(source) }
        val distances = ArrayList<Float>().apply { add(0f) }
        val edgeTo = ArrayList<Int>().apply { add(0) }

        fun pushBest(nodes: List<Node>, index: Int) {
            if (nodes.isNotEmpty()) {
                val point = vertices[index]
                val closest = Utils.closestPoint(nodes, target)
                val distance = distances[index] + Utils.distance(point, closest)
                val heuristic = distance + Utils.distance(closest, target)
                fringe.offer(Pair(heuristic, vertices.size))
                vertices.add(closest)
                distances.add(distance)
                edgeTo.add(index)
            }
        }

        fun pushNeighbors(index: Int) {
            val point = vertices[index]
            val xError = target.x - point.x
            val yError = target.y - point.y
            val delta = Config.moveThreshold / sqrt(2f)

            if (abs(xError) > Config.moveThreshold) {
                val xMin = if (xError > 0) point.x + Config.moveThreshold / 4 else point.x - Config.moveThreshold * 2
                val xMax = if (xError > 0) point.x + Config.moveThreshold * 2 else point.x - Config.moveThreshold / 4
                val candidates = search(xMin, xMax, point.y - delta, point.y + delta)
                pushBest(candidates, index)
            }

            if (abs(yError) > Config.moveThreshold) {
                val yMin = if (yError > 0) point.y + Config.moveThreshold / 4 else 0f
                val yMax = if (yError > 0) 1f else point.y - Config.moveThreshold / 4
                val candidates = search(point.x - delta, point.x + delta, yMin, yMax)
                pushBest(candidates, index)
            }
        }

        var i = 0
        while (Utils.distance(vertices[i], target) > Config.moveThreshold) {
            pushNeighbors(i)
            if (fringe.isEmpty()) break
            i = fringe.poll().second
        }

        val path = ArrayList<Node>().apply { add(target) }
        while (i != 0) {
            path.add(vertices[i])
            i = edgeTo[i]
        }
        path.add(source)

        path.reverse()
        Config.layout = path
        return path
    }

    private fun search(xMin: Float, xMax: Float, yMin: Float, yMax: Float): List<Node> {
        val nodes = ArrayList<Node>()

        fun searchHelper(node: Node?) {
            node?.let {
                if (it.x in xMin..xMax && yMin <= it.y && it.y <= yMax) {
                    nodes.add(it)
                }
                if (xMin < it.x) {
                    if (yMin < it.y) searchHelper(it.downLeft)
                    if (yMax >= it.y) searchHelper(it.upLeft)
                }
                if (xMax >= it.x) {
                    if (yMin < it.y) searchHelper(it.downRight)
                    if (yMax >= it.y) searchHelper(it.upRight)
                }
            }
        }

        searchHelper(root)
        return nodes
    }
    fun add(x: Float, y: Float) {
        fun addHelper(node: Node?): Node {
            return if (node == null) {
                Node(x, y)
            } else {
                when {
                    y >= node.y && x < node.x -> node.apply { upLeft = addHelper(upLeft) }
                    y >= node.y && x >= node.x -> node.apply { upRight = addHelper(upRight) }
                    y < node.y && x < node.x -> node.apply { downLeft = addHelper(downLeft) }
                    else -> node.apply { downRight = addHelper(downRight) }
                }
            }
        }

        fun checkCollision(point: Node): Boolean {
            return Utils.distance(point, Node(x, y)) >= tolerance
        }

        val checks = this.search(
            x - tolerance,
            x + tolerance,
            y - tolerance,
            y + tolerance
        ).map { checkCollision(it) }

        if (checks.all { it }) {
            this.root = addHelper(this.root)
        }
    }
}