package fr.perso.labyrinth.board.algorithm.dataMap

import fr.perso.labyrinth.board.Board
import fr.perso.labyrinth.board.BoardZone
import fr.perso.labyrinth.board.Point
import fr.perso.labyrinth.board.PointImpl



fun <T : BoardZone> distanceMap(start: Point, board: Board<T>): Map<T, Int> {
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


