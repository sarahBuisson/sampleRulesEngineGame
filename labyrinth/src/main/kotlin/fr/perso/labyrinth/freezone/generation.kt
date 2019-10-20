package fr.perso.labyrinth.freezone.generation


import fr.perso.labyrinth.ConnectedZone
import fr.perso.labyrinth.GeoZone
import fr.perso.labyrinth.NamedZone
import doorWithKeyDefault
import objetDiversDefault

/** draw a lab where zone are linked each to another, without notion of x-y*/
var index = 0;


interface FreeZone:NamedZone, ConnectedZone, GeoZone {
    override val name: String
    override var connected: MutableList<FreeZone>
    override var content: MutableList<ObjectZone>
}


data class FreeZoneImpl(
    override val name: String = "" + (index++),
    override var connected: MutableList<FreeZone> = mutableListOf<FreeZone>(),
    override var content: MutableList<ObjectZone> = mutableListOf<ObjectZone>()


):FreeZone {

    override fun toString(): String {
        return name + "(" + connected.map { it.name } + ")" + content.map { it.name } + ""
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}


open class ObjectZone(open var name: String) {
    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is ObjectZone)
            return name.equals(other.name)
        return false
    }
}

class DoorObjectZone(val destination: ConnectedZone, var key: ObjectZone? = null) : ObjectZone("door")
class KeyObjectZone(override var name: String) : ObjectZone(name)
class ExchangeObjectZone(
    var want: ObjectZone,
    var give: ObjectZone,
    override var name: String = " exchange donne ${give.name} contre ${want.name}"
) : ObjectZone(name)


fun linkZone(a: FreeZone, b: FreeZone) {
    a.connected.add(b)
    b.connected.add(a)
}

fun createCorridor(size: Int): List<FreeZone> {
    val corridor = mutableListOf<FreeZone>(FreeZoneImpl())
    for (i in 0..size) {
        val newZone = FreeZoneImpl()

        linkZone(newZone, corridor.last())
        corridor.add(newZone);

    }
    return corridor
}

fun createLab(size: Int): List<FreeZone> {
    val lab = mutableListOf<FreeZone>()
    val solution = createCorridor(size);
    lab.addAll(solution)

    lab.toList().forEach { it ->
        if (it.connected.size < 4 &&
            (1..10).random() > 8
        ) {
            val culDeSac = createCorridor(size)
            linkZone(culDeSac.first(), it)
            lab.addAll(culDeSac);
        }
    }
    return lab
}


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


class LabFillerExit<T>(
    keyToDoorArray: Array<Array<String>> = doorWithKeyDefault,
    objetDiversArray: Array<String> = objetDiversDefault
) :
    LabFiller<T>(keyToDoorArray, objetDiversArray)
        where T : GeoZone, T : ConnectedZone {

    fun isCulDeSac(freeZone: ConnectedZone): Boolean {
        val distance = distanceMap[freeZone]!!
        return freeZone.connected.all { distanceMap[it]!! < distance }

    }

    override public fun fillLab(
        zones: List<T>, start:T, numberOfDoor: Int, numberOfExchanges: Int
    ) {
        val culDeSacs = zones.filter { isCulDeSac(it) }.sortedByDescending { distanceMap[it] };
        val exit = culDeSacs.first()
        exit.content.add(KeyObjectZone("victoire"))

        val pathToExit = mutableListOf<T>()
        var cursor = exit
        do {
            cursor = cursor.connected.minBy { distanceMap[it]!! }!! as T
            pathToExit.add(cursor)
        } while (distanceMap[cursor]!! > 0)

        for (i in pathToExit.size - 1..0) {
            val current = pathToExit[i]!!
            val lockedZone = pathToExit[i - 1]!!
            val doorToExit = current.content.filterIsInstance<DoorObjectZone>().find { it.destination == lockedZone }!!

            this.affectKeyToDoor(doorToExit, current)

        }
        super.fillLab(zones, start, numberOfDoor, numberOfExchanges)
    }

}


open class LabFiller<T>
        where T : GeoZone, T : ConnectedZone {

   lateinit var distanceMap: Map<ConnectedZone, Int>
    lateinit var begin: T
    var keyToDoor: MutableList<Pair<String, String>>;
    var objetDivers: MutableList<String>;
    var listOfKey: MutableList<String>;

    constructor(
        keyToDoorArray: Array<Array<String>> = doorWithKeyDefault,
        objetDiversArray: Array<String> = objetDiversDefault
    ) {
        keyToDoor = keyToDoorArray.map { Pair(it[0], it[1]) }.toMutableList()
        objetDivers = objetDiversArray.toMutableList()
        listOfKey = keyToDoor.map { it.second }.toMutableList()
    }


    open public fun fillLab(
        zones: List<T>,  begin:T = zones.first(), numberOfDoor: Int, numberOfExchanges: Int
    ) {

        distanceMap = distanceToZone(begin);

        zones.forEach { zone ->
            zone.connected.forEach {

                zone.content.add(DoorObjectZone(it));
            }
        }


        for (i in 1..numberOfDoor) {


            lateinit var availableZoneForKey: Set<T>;
            lateinit var zoneWhoWillBeClosedByDoorAndKey: T;
            lateinit var doorZone: T;
            lateinit var zonesAfterDoorZone: List<T>;


            //Select a zone when you can put a door.
            do {
                doorZone = zones.random()
                val distanceMax = distanceMap[doorZone]!!
                zonesAfterDoorZone = doorZone.connected.filter { distanceMap[it]!! > distanceMax } as List<T>
            } while (zonesAfterDoorZone.isEmpty())

            val distanceMax = distanceMap[doorZone]!!;
            zoneWhoWillBeClosedByDoorAndKey = zonesAfterDoorZone.random()
            val door =
                doorZone.content.find { it is DoorObjectZone && it.destination == zoneWhoWillBeClosedByDoorAndKey } as DoorObjectZone

            affectKeyToDoor(
                door, doorZone

            )
        }

        //And now the exchanges

        for (i in 0..numberOfExchanges) {
            val zoneWithKey = zones.filter { it.content.any { it is KeyObjectZone } }.random()
            val keyToExchange = zoneWithKey.content.filter { it is KeyObjectZone }.random()
            val distanceMax = distanceMap[zoneWithKey]!!
            val zonesAvailable = zones.filter { distanceMap[it]!! <= distanceMax }

            val zoneOfNewObject = zonesAvailable.random();

            val newObjectToExchange = generateKey()
            val exchanger = ExchangeObjectZone(want = newObjectToExchange, give = keyToExchange)
            zoneWithKey.content.remove(keyToExchange);
            zoneWithKey.content.add(exchanger);
            zoneOfNewObject.content.add(newObjectToExchange)


        }


    }


    protected  fun affectKeyToDoor(
        door: DoorObjectZone,
        doorZone: T
    ) {
        val distanceMax = distanceMap[doorZone]!!
        val availableZoneForKey = distanceMap.filterValues { it <= distanceMax }.keys
        val keyZone = availableZoneForKey.random() as GeoZone;

        val key = generateKey()

        affectKeyToDoor(door, key)

        keyZone.content.add(key)
    }

    protected fun affectKeyToDoor(
        door: DoorObjectZone,
        key: KeyObjectZone
    ) {

        door.key = key
        door.name = keyToDoor.find { it.second == key.name }!!.first
    }


    private fun generateKey(
    ): KeyObjectZone {
        val name = listOfKey.random()
        listOfKey.remove(name)
        return KeyObjectZone(name)
    }

}
