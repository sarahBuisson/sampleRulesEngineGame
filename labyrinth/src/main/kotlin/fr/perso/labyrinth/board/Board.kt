package fr.perso.labyrinth.board

import fr.perso.labyrinth.ConnectedZone


interface BoardZone : ConnectedZone, Point {
    val connections: MutableMap<Direction, BoardZone>
}

open class BoardZoneImpl(override val x: Int, override val y: Int) : PointImpl(x, y), BoardZone, ConnectedZone {
    override val connections = mutableMapOf<Direction, BoardZone>()
    override val connected: List<ConnectedZone>
        get() = connections.values.toList()


}


fun connectZone(
        oldPosition: BoardZone,
        newPosition: BoardZone,
        direction: Direction) {
    newPosition.connections.put(direction.inv(), oldPosition)
    oldPosition.connections.put(direction, newPosition)
}


fun unconnectZone(oldPosition: BoardZone, newPosition: BoardZone) {
    val direction = newPosition.connections.filterValues { it == oldPosition }.keys.first();
    newPosition.connections.remove(direction)
    oldPosition.connections.remove(direction.inv())

}


class Board<T : Any> {
    lateinit var start: T
    var exit: T? = null
    val width: Int
    val height: Int

    val content: MutableList<MutableList<T>> = mutableListOf<MutableList<T>>();

    constructor(width: Int, height: Int, factory: (x: Int, y: Int, board: Board<T>) -> T) {
        this.width = width
        this.height = height
        for (y in 0..height) {
            content.add(mutableListOf())
            for (x in 0..width) {

                content.get(y).add(factory(x, y, this))

            }
        }
    }


    fun get(x: Int, y: Int): T? {
        if (x in 0..width && y in 0..height) return content.get(y)?.get(x) else return null
    }

    fun get(p: Point): T? = get(p.x, p.y)

    fun getNeigbour(x: Int, y: Int, dx: Int, dy: Int): T? = get(x + dx, y + dy)
    fun getNeigbour(p: Point, d: Direction): T? = get(p.x + d.x, p.y + d.y)
    fun getNeigbours(p: Point): List<T> = Direction.values().mapNotNull { this.getNeigbour(p, it) }
    fun getNeigboursMap(p: Point): Map<Direction, T> =
            Direction.values().associate { Pair(it, this.getNeigbour(p, it)) }.filter { it.value != null }
                    .mapValues { it.value!! }

    fun toString(toString: (it: T) -> String): String {
        return content.map { "\n" + it.map { it2: T -> " " + toString(it2) } }.toString()
    }

    fun toList(): List<T> = content.flatMap { it }

}

interface Point {
    val x: Int
    val y: Int
}

open class PointImpl(override val x: Int, override val y: Int) : Point {

    fun add(x: Int, y: Int): PointImpl = PointImpl(this.x + x, this.y + y)
    fun add(p2: PointImpl): PointImpl =
            PointImpl(this.x + p2.x, this.y + p2.y)

    override fun toString(): String {
        return "($x, $y)"
    }

}

enum class Direction(val x: Int, val y: Int) {
    LEFT(-1, 0),
    TOP(0, -1),
    RIGHT(1, 0),
    BOTTOM(0, 1);

    fun times(n: Int): PointImpl = PointImpl(x * n, y * n)
    fun inv(): Direction = when (this) {
        LEFT -> RIGHT
        RIGHT -> LEFT
        TOP -> BOTTOM
        BOTTOM -> TOP

    }
}


val defaultZoneName: (BoardZone) -> Any? = {
    when {
        it.connections.size > 0 -> '+'
        else -> ' '
    }
}


val defaultDoorName: (Direction, BoardZone) -> Any = { d, zone ->
    val show = zone.connections.containsKey(d)
    when {
        d == Direction.TOP && show -> "|"
        d == Direction.TOP && !show -> " "
        d == Direction.BOTTOM && show -> "|"
        d == Direction.BOTTOM && !show -> " "
        d == Direction.LEFT && show -> '-'
        d == Direction.LEFT && !show -> ' '
        d == Direction.RIGHT && show -> '-'
        d == Direction.RIGHT && !show -> ' '
        else -> '?'
    }
}

fun <T : BoardZone> labyrinthTreeToString(board: Board<T>, zoneName: (T) -> Any? = defaultZoneName, doorName: (Direction, T) -> Any? = defaultDoorName): String {


    var str = "";

    str = board.content.map {


        val horizontal: String = it.map { zone ->
            var r: String = ""

            r += doorName(Direction.LEFT, zone)

            if (zone == board.start) {
                r += "@"
            } else if (zone == board.exit) {
                r += "€"
            } else {
                r += zoneName(zone)
            }
            r += doorName(Direction.RIGHT, zone);

            r

        }.joinToString("")


        val verticalTop = it.map { zone ->
            " ${doorName(Direction.TOP, zone)} "
        }.joinToString("")

        val verticalBottom = it.map { zone ->
            " ${doorName(Direction.BOTTOM, zone)} "
        }.joinToString("")
        verticalTop + "\n" + horizontal + "\n" + verticalBottom


    }.joinToString("\n")
    return str;
}
