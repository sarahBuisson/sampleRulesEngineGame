package fr.perso.labyrinth.board.algorithm

import fr.perso.labyrinth.board.*
import org.jeasy.rules.api.Condition

fun <T : BoardZone> drawDirectCoridorH(board: Board<out T>, begin: PointImpl, end: PointImpl): List<T> {


    var coridor = mutableListOf<T>();


    for (x in begin.x..end.x)
        coridor.add(board.get(x, begin.y)!!)

    coridor.remove(coridor.last())
    for (y in begin.y..end.y)
        coridor.add(board.get(end.x, y)!!)

    return coridor;
}


fun <T : ZoneOfCoridor> drawComplexCoridorH(board: Board<out T>, begin: PointImpl, end: PointImpl): List<T> {


    var coridor = drawDirectCoridorH<T>(board, begin, end);


    coridor = complexifyRandomlyCoridor(coridor, board)
    coridor = complexifyRandomlyCoridor(coridor, board)
    coridor = complexifyRandomlyCoridor(coridor, board)
    coridor = complexifyCoridor(coridor, board)

    return coridor;
}

private fun <T : ZoneOfCoridor> complexifyCoridor(
        coridor: List<out T>,
        board: Board<out T>
): List<T> {
    var coridor1 = coridor
    coridor1.forEach {
        coridor1 = addComplexityToCoridorAtElement(board, coridor1, it)
    }
    return coridor1
}

private fun <T : ZoneOfCoridor> complexifyRandomlyCoridor(
        coridor: List<out T>,
        board: Board<out T>
): List<T> {
    var coridor1 = coridor
    var size = coridor1.size - 1
    while (coridor1.size > size) {
        coridor1.shuffled().forEach {
            coridor1 = addComplexityToCoridorAtElement(board, coridor1, it)
        }
        size = coridor1.size;
    }
    return coridor1
}

fun <T : ZoneOfCoridor> addComplexityToCoridorAtElement(board: Board<out T>, coridor: List<out T>, element: T): List<T> {

    var coridor = coridor
    var beginComplexity = element;
    var randomDir = Direction.values().random().times(1)

    var randomLen = (1..board.content.size).random()
    val index = coridor.indexOf(beginComplexity) + 1
    if (index >= coridor.size)
        return coridor;
    var endComplexity = coridor.get(index);


    var beginExtension = board.get(beginComplexity.add(randomDir));
    var endExtension = board.get(endComplexity.add(randomDir));

    var extentionExistAndNotAlreadyOnCoridor =
            beginExtension != null && endExtension != null && !coridor.contains(beginExtension) && !coridor.contains(
                    endExtension
            )
    while (extentionExistAndNotAlreadyOnCoridor && randomLen > 0) {
        coridor =
                insertAfter(coridor, beginComplexity, beginExtension!!, endExtension!!)

        //the newly added extension becomme the new start of the added complexity, so the complexityContinueToGrow
        beginComplexity = beginExtension;
        endComplexity = endExtension;
        beginExtension = board.get(beginComplexity.add(randomDir));
        endExtension = board.get(endComplexity.add(randomDir));
        randomLen--;
        extentionExistAndNotAlreadyOnCoridor =
                beginExtension != null && endExtension != null && !coridor.contains(beginExtension) && !coridor.contains(
                        endExtension
                )
    }


    return coridor;
}


fun <T : ZoneOfCoridor> addDeadEndToElementOfCoridor(board: Board<T>, element: T): List<T> {

    var deadEnd = mutableListOf<T>(element)
    var beginComplexity = element;
    var randomDir = Direction.values().random().times(1)

    var randomLen = (1..board.content.size).random() + 2


    var beginExtension = board.get(beginComplexity.add(randomDir))

    var extentionExistAndNotAlreadyOnCoridor = beginExtension != null && !deadEnd.contains(beginExtension)
            && beginExtension.haveBeenVisited == 0;

    while (extentionExistAndNotAlreadyOnCoridor && randomLen > 0) {
        deadEnd.add(beginExtension!!)

        //the newly added extension becomme the new start of the added complexity, so the complexityContinueToGrow
        beginComplexity = beginExtension;
        beginExtension = board.get(beginComplexity.add(randomDir))

        randomLen--;
        extentionExistAndNotAlreadyOnCoridor = beginExtension != null && !deadEnd.contains(beginExtension)
                && beginExtension.haveBeenVisited == 0;

    }


    return deadEnd;
}


fun <T> insertAfter(iter: List<T>, after: T, vararg data: T): List<T> {
    val index = iter.indexOf(after) + 1
    return iter.subList(0, index) + data + iter.subList(index, iter.size)
}


class ZoneOfCoridor(x: Int, y: Int) : BoardZoneImpl(x, y) {
    val connectedZoneAndWayToGo = mutableMapOf<BoardZone, Condition<Partie>>()
    var haveBeenVisited: Int = 0
    override fun toString(): String {
        return "" + haveBeenVisited
    }

    val opens = mutableListOf<Direction>()
}

fun drawLabyrinth(board: Board<ZoneOfCoridor>): MutableList<List<ZoneOfCoridor>> {
    val size = board.content.size;
    val coridors = mutableListOf<List<ZoneOfCoridor>>();


    var solution = drawDirectCoridorH(
            board,
            PointImpl(2, 2),
            PointImpl(size - 2, size - 2)
    )


    solution = complexifyRandomlyCoridor(solution, board)




    coridors.add(solution)
    println(labyrinthToString(board, coridors))

    for (i in 0..3) {

        coridors.flatten().forEachIndexed { index, zone -> zone.haveBeenVisited = index + 1 }

        coridors.flatten().shuffled().forEach { coridor ->
            var deadEnd = addDeadEndToElementOfCoridor(board, coridor)
            if (deadEnd.size > 1) {//only keep big deadEnd

                deadEnd = complexifyRandomlyCoridor(deadEnd, board);
                deadEnd.forEachIndexed { index, zone -> zone.haveBeenVisited = index + 1 }
                coridors.add(deadEnd)
            }


        }

    }




    return coridors;
}


fun labyrinthToString(board: Board<ZoneOfCoridor>, coridors: List<List<ZoneOfCoridor>>): String {

    coridors.flatten().forEach { it.opens.clear() }
    coridors.forEach { cori ->

        for (i in 0..(cori.size - 2)) {
            val zone = cori[i];
            val next = cori[i + 1];
            Direction.values().forEach { dir ->

                val neigbour = board.getNeigbour(zone, dir)
                if (next == (neigbour)) {
                    zone.opens.add(dir)
                    next.opens.add(dir.inv())
                }
            }

        }

    }
    var str = "";

    str = board.content.map {


        val horizontal: String = it.map { zone ->
            var r: String = ""
            if (zone.opens.contains(Direction.LEFT)) {
                r += "- ";
            } else {
                r += "  ";
            }
            if (zone.opens.contains(Direction.RIGHT)) {
                r += "-";
            } else {
                r += " ";
            }

            r

        }.joinToString("")


        val verticalTop = it.map { zone ->

            if (zone.opens.contains(Direction.TOP)) {
                " | ";
            } else {
                "   ";
            }

        }.joinToString("")

        val verticalBottom = it.map { zone ->

            if (zone.opens.contains(Direction.BOTTOM)) {
                " | ";
            } else {
                "   ";
            }

        }.joinToString("")
        verticalBottom + "\n" + horizontal + "\n" + verticalTop


    }.joinToString("\n");
    return str;
}
