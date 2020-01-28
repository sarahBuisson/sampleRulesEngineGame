package fr.perso.labyrinth.freezone.generation

import fr.perso.labyrinth.ConnectedZone
import fr.perso.labyrinth.board.algorithm.followCorridor


fun distanceToZone(
        origin: ConnectedZone,
        count: MutableMap<ConnectedZone, Int> = mutableMapOf<ConnectedZone, Int>(Pair(origin, 0))
): MutableMap<ConnectedZone, Int> {
    if (count[origin] == null) {
        val min: Int = origin.connected.map { count[it] }.filterNotNull().min() ?: 0
        count.put(origin, min + 1)
    }
    origin.connected.forEach { connected ->
        if (!count.containsKey(connected)) {
            distanceToZone(connected, count)
        }
    }
    return count;
}


fun corridorIdMap(zones: List<ConnectedZone>
): MutableMap<ConnectedZone, Int> {

    var corridorIdCount = 1
    val ret = mutableMapOf<ConnectedZone, Int>()

    zones.filter { it.connected.size > 2 }.forEach {

        ret.put(it, 0);//intersection are marked as corridor id 0
        it.connected.forEach { connected ->
            if (!ret.containsKey(connected)) {
                val corridorId = corridorIdCount++
                ret.put(connected, corridorId)
                followCorridor(connected).filter { it.connected.size <= 2 }.forEach {
                    ret.put(connected, corridorId)
                }
            }
        }
    }




    return ret;
}


fun corridorLength(zones: List<ConnectedZone>
): MutableMap<ConnectedZone, Int> {

    var corridorIdCount = 1
    val ret = mutableMapOf<ConnectedZone, Int>()

    zones.filter { it.connected.size > 2 }.forEach {

        ret.put(it, 0);//intersection are marked as corridor id 0
        it.connected.forEach { connected ->
            if (!ret.containsKey(connected)) {
                val corridorId = corridorIdCount++
                ret.put(connected, corridorId)
                followCorridor(connected).filter { it.connected.size <= 2 }.forEach {
                    ret.put(connected, corridorId)
                }
            }
        }
    }




    return ret;
}
