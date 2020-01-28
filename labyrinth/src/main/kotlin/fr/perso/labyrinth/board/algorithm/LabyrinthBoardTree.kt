package fr.perso.labyrinth.board.algorithm

import fr.perso.labyrinth.ConnectedZone
import fr.perso.labyrinth.board.*
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.DefaultRulesEngine
import org.jeasy.rules.core.LambdaRule


class DrawLabFacts<T : BoardZone>(val board: Board<T>)
class DrawLabCaseFacts<T : BoardZone>(val zone: BoardZone, val board: Board<T>)


val ruleConnectEndCaseToAFreeNeighboor = LambdaRule<DrawLabCaseFacts<BoardZone>>({ facts ->
    facts.zone.connections.size == 1


}, { facts ->
    val freeNeighboors =
            facts.board.getNeigboursMap(facts.zone).filter { it.value != null }.filter { it.value!!.connections.size == 0 }
    if (freeNeighboors.isNotEmpty()) {
        val nextNei = freeNeighboors.entries.random();
        facts.zone.connectZone(nextNei.value!!, nextNei.key)
    }

});


val ruleAddCrossToAFreeNeighboor = LambdaRule<DrawLabCaseFacts<BoardZone>>({ facts ->
    true


}, { facts ->
    val freeNeighboors =
            facts.board.getNeigboursMap(facts.zone).filter { it.value != null }.filter { it.value!!.connections.size == 0 }
    if (freeNeighboors.isNotEmpty()) {
        val nextNei = freeNeighboors.entries.random();
        facts.zone.connectZone(nextNei.value!!, nextNei.key)
    }

});


val ruleConnectUnconnectedCaseToAnyConnectedNei = LambdaRule<DrawLabCaseFacts<BoardZone>>({ facts ->
    facts.zone.connections.size == 0


}, { facts ->
    val freeNeighboors =
            facts.board.getNeigboursMap(facts.zone).filter { it.value != null }.filter { it.value!!.connections.size > 0 }

    val nextNei = freeNeighboors.entries.random();
    facts.zone.connectZone(nextNei.value!!, nextNei.key)

});


val ruleConnectUnconnectedCaseToBestConnectedNei = LambdaRule<DrawLabCaseFacts<BoardZone>>({ facts ->
    facts.zone.connections.size == 0


}, { facts ->
    val freeNeighboors =
            facts.board.getNeigboursMap(facts.zone).filter { it.value != null }.filter { it.value!!.connections.size > 0 }
    if (freeNeighboors.isNotEmpty()) {
        val nextNei = freeNeighboors.entries.sortedBy { it.value.connections.size }.last();
        facts.zone.connectZone(nextNei.value!!, nextNei.key)
    }

});

fun <T> runBookD(fact: T, rules: Rules<T>) {
    DefaultRulesEngine<T>().fire(rules, fact)
}

fun <T : BoardZone> drawLab(board: Board<T>): Board<T> {

    board.start = board.toList().random()


    val firstC = board.getNeigboursMap(board.start).entries.random()
    board.start.connectZone(firstC.value!!, firstC.key);
    var countFreeCase = board.toList().size;
    val rules = Rules(setOf(
            ruleConnectEndCaseToAFreeNeighboor,
            ruleConnectUnconnectedCaseToBestConnectedNei
    ))
    do {
        for (case in board.toList().shuffled()) {
            val facts = DrawLabCaseFacts(case, board)
            runBookD(facts, rules as Rules<DrawLabCaseFacts<T>>)


        }
        val previousCount = countFreeCase
        countFreeCase = board.toList().count { it.connections.size == 0 }
        println(countFreeCase)
        println(labyrinthTreeToString(board))
    } while (countFreeCase < previousCount)

    println("before mergeCul")
    println(labyrinthTreeToString(board))
    complexiteMergeCulDeSac(board)
    println(labyrinthTreeToString(board))
    complexiteMergeCulDeSac(board)
    println("adter mergeCul")

    println(labyrinthTreeToString(board))
    return board
}

fun <T> chooseStartExit(board: Board<T>)
        where T : BoardZone, T : Point {
    val mapDistance = distance(board.start, board)
    board.exit = mapDistance.entries.maxBy { it.value }!!.key
    val mapDistanceS = distance(board.exit!!, board)
    board.start = mapDistanceS.entries.maxBy { it.value }!!.key

}


fun <T : BoardZone> distance(start: Point, board: Board<T>): Map<T, Int> {
    val distance = mutableMapOf<T, Int>()
    distance.put(board.get(start)!!, 0)
    val zones = board.toList()
    while (distance.size < zones.size) {

        zones.forEach { currentZone ->
            val pos = PointImpl(currentZone.x, currentZone.y)
            val dist = distance.get(board.get(pos)!!)

            val currentNei =
                    board.getNeigboursMap(pos).filter { currentZone.connections.containsValue(it.value) }


            if (dist != null) {

                currentNei.forEach { entry ->
                    val neigbour = entry.value!!
                    val nDist = distance.get(board.get(neigbour.x, neigbour.y)!!);
                    if (nDist == null || (nDist > (1 + dist))) {
                        distance.put(board.get(entry.value)!!, dist + 1);
                    }
                }
            }
        }
    }
    return distance
}

fun <T : BoardZone> coridorSizeDistanceMap(board: Board<T>): Map<T, Int> {
    val distance = mutableMapOf<T, Int>()
    //distance.put(board.get(start)!!, 0)
    val zones = board.toList()
    while (distance.size < zones.size) {

        zones.forEach { currentZone ->
            val pos = PointImpl(currentZone.x, currentZone.y)

            val currentNei =
                    board.getNeigboursMap(pos).filter { currentZone.connections.containsValue(it.value) }

            if (currentNei.size > 2) {
                distance.put(board.get(pos)!!, 0)
            }

            val dist = distance.get(board.get(pos)!!)
            if (dist != null) {

                currentNei.forEach { entry ->
                    val neigbour = entry.value!!
                    val nDist = distance.get(board.get(neigbour.x, neigbour.y)!!);
                    if (nDist == null || (nDist > (1 + dist))) {
                        distance.put(board.get(entry.value)!!, dist + 1);
                    }
                }
            }
        }
    }
    return distance
}

/*
* merge two cul de sac corridor connected into one cul de sac
* */
fun <T : BoardZone> complexiteMergeCulDeSac(board: Board<T>) {
    val culDeSac = board.toList().filter { it.connected.size == 1 }

    culDeSac.shuffled().forEach { currentCulDeSac ->

       //if is still a culDeSac

        if(currentCulDeSac.connected.size==1) {
            val nearestCulDeSacs = board.getNeigbours(currentCulDeSac).filter { nei ->
                nei.connected.size == 1//are cul de sac
                        && !nei.connected.contains(currentCulDeSac)//are not already linked to the current cul de sac
            }
            if (nearestCulDeSacs.size > 0) {
                val corridors = nearestCulDeSacs.map { culDeSac -> followCorridor(culDeSac) }.filter { it.isNotEmpty() }
                if (corridors.isNotEmpty()) {
                    val corridorToMerge = corridors.maxBy { it.size }!!

                    val unconnect = corridorToMerge.last().connected.find { it.connected.size > 2 }!!
                    currentCulDeSac.connectTo(corridorToMerge.first())
                    corridorToMerge.last().unconnectTo(unconnect)
                } else {
                    println("no merge because too short $currentCulDeSac")
                    println(nearestCulDeSacs.map { it.toString() }.joinToString("\n"))
                    println(nearestCulDeSacs.map { culDeSac -> followCorridor(culDeSac) }.map { it.toString() }.joinToString("\n"))

                }
            } else {
                println("no merge because no near $currentCulDeSac")

            }
        }
    }


}

fun followCorridor(start: ConnectedZone): Set<ConnectedZone> {
    val ret = mutableSetOf<ConnectedZone>()
    var next: ConnectedZone? = start
    while (next != null && next.connected.size <= 2) {
        ret.add(next)
        next = next.connected.find { !ret.contains(it) }
    }
    return ret;
}


fun complexiteMap(start: Point, board: Board<BoardZone>): Map<BoardZone, Int> {
    val complexite = mutableMapOf<BoardZone, Int>()
    complexite.put(board.get(start)!!, 0)
    while (complexite.size < board.toList().size) {

        board.toList().forEach { currentZone ->
            val pos = PointImpl(currentZone.x, currentZone.y)
            val currentComplexite = complexite.get(board.get(pos)!!)
            val currentNei =
                    board.getNeigboursMap(pos).filter { currentZone.connections.containsValue(it.value) }
            if (currentComplexite != null) {

                currentNei.forEach { entry ->
                    val neigbour = entry.value!!
                    val neiDirection = entry.key!!
                    val neiComplexite = complexite.get(board.get(neigbour.x, neigbour.y)!!);


                    val neiOfNeighbour =
                            board.getNeigboursMap(PointImpl(neigbour.x, neigbour.x))
                                    .filter { neigbour.connections.containsValue(it.value) }

                    val commonNeiDirection: Int;
                    if (neiOfNeighbour.containsKey(neiDirection))
                        commonNeiDirection = 1
                    else commonNeiDirection = 0;
                    val biffurcation: Int
                    if (neiOfNeighbour.size > 2)
                        biffurcation = 1
                    else biffurcation = 0;
                    val newComplexite = 1 + commonNeiDirection + biffurcation



                    if (neiComplexite == null || (neiComplexite > newComplexite)) {
                        complexite.put(board.get(entry.value)!!, currentComplexite + 1);
                    }
                }
            }
        }
    }
    return complexite
}

/**
 * transforme a Map of the distance or anything by zone to a 2D array
 */
fun distanceToArray(distance: Map<BoardZone, Int>, board: Board<BoardZone>): Array<Array<Int>> {
    val array: Array<Array<Int>> = Array<Array<Int>>(board.height, { Array(board.width, { 0 }) });


    distance.forEach { array[it.key.y][it.key.x] = it.value }

    return array;


}

