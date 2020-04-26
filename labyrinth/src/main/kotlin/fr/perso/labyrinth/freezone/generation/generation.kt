package fr.perso.labyrinth.freezone.generation


import fr.perso.labyrinth.ConnectedZone
import fr.perso.labyrinth.GeoZone
import doorWithKeyDefault
import fr.perso.labyrinth.board.Board
import fr.perso.labyrinth.freezone.model.DoorObjectZone
import fr.perso.labyrinth.freezone.model.ExchangeObjectZone
import fr.perso.labyrinth.freezone.model.KeyObjectZone
import objetDiversDefault
import kotlin.properties.Delegates

/** draw a lab where zone are linked each to another, without notion of x-y
 *
 * and with an "exit, a place with a "victoire" object
 * */


open class LabFillerExit<T>(
        keyToDoorArray: Array<Array<String>> = doorWithKeyDefault,
        objetDiversArray: Array<String> = objetDiversDefault
) :
        LabFiller<T>(keyToDoorArray, objetDiversArray)
        where T : GeoZone, T : ConnectedZone {

    fun isCulDeSac(freeZone: ConnectedZone): Boolean {
        val distance = distanceFromStartMap[freeZone]!!
        return freeZone.connected.all { distanceFromStartMap[it]!! < distance }

    }

    override fun fillLab(
    ) {
        exit!!.content.add(KeyObjectZone("victoire"))

        val pathToExit = extractPathFromStartToExit(exit)
        fillPathWithClosedDoor(pathToExit, listOfKey.subList(0, listOfKey.size / 2))
        println("pathFilled")
        fillLabWithDoors(listOfKey.subList(listOfKey.size / 2, listOfKey.size))


    }

    private fun fillPathWithClosedDoor(pathToExit: MutableList<T>, listOfKey: MutableList<String>) {
        val inter = IntRange(1, pathToExit.size - 1).shuffled();
        for (i in inter) {
            val currentZone = pathToExit[i]!!
            val lockedZone = pathToExit[i - 1]!!
            println(currentZone)
            println(lockedZone)
            val doorToExit = currentZone.content.filterIsInstance<DoorObjectZone>().find { it.destination == lockedZone }


            if (doorToExit != null) {
                this.affectKeyToDoor(doorToExit, currentZone, listOfKey)
            }
            if (listOfKey.isEmpty())
                return;

        }

    }

    private fun extractPathFromStartToExit(exit: T?): MutableList<T> {
        val pathToExit = mutableListOf<T>()
        var cursor = exit!!
        do {
            cursor = cursor.connected.minBy { distanceFromStartMap[it]!! }!! as T
            pathToExit.add(cursor)
        } while (distanceFromStartMap[cursor]!! > 0)
        return pathToExit
    }

}


open class LabFiller<TZone>
        where TZone : GeoZone, TZone : ConnectedZone {

     lateinit var zones: List<TZone>
    lateinit var distanceFromStartMap: Map<ConnectedZone, Int>
    var numberOfDoor by Delegates.notNull<Int>()
    var numberOfExchanges by Delegates.notNull<Int>()
    lateinit var begin: TZone
    lateinit var exit: TZone
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


    open public fun init(zones: List<TZone>, begin: TZone = zones.first(), exit: TZone = zones.last(), numberOfDoor: Int, numberOfExchanges: Int): LabFiller<TZone> {

        this.begin=begin
        this.exit=exit
        distanceFromStartMap = distanceToZone(begin);
        this.zones=zones;
        this.numberOfDoor=numberOfDoor;
        this.numberOfExchanges=numberOfExchanges;

        zones.forEach { zone ->
            zone.connected.forEach {

                zone.content.add(DoorObjectZone(it));
            }
        }
        return this;
    }

    open public fun fillLab(
            ) {


        fillLabWithDoors(this.listOfKey)

        //And now the exchanges

        fillLabWithExchanges(numberOfExchanges, zones)


    }

    private fun fillLabWithExchanges(numberOfExchanges: Int, zones: List<TZone>) {
        for (i in 1..numberOfExchanges) {
            val zoneWithKey = zones.filter { it.content.any { it is KeyObjectZone } }.random()
            val keyToExchange = zoneWithKey.content.filter { it is KeyObjectZone }.random()
            val distanceMax = distanceFromStartMap[zoneWithKey]!!
            val zonesAvailable = zones.filter { distanceFromStartMap[it]!! <= distanceMax }

            val zoneOfNewObject = zonesAvailable.random();

            val newObjectToExchange = generateKey(this.listOfKey)
            val exchanger = ExchangeObjectZone(want = newObjectToExchange, give = keyToExchange)
            zoneWithKey.content.remove(keyToExchange);
            zoneWithKey.content.add(exchanger);
            zoneOfNewObject.content.add(newObjectToExchange)


        }
    }

    protected fun fillLabWithDoors(listOfKey: MutableList<String>) {
        for (i in 1..numberOfDoor) {
            if (listOfKey.isEmpty())
                return

            lateinit var zoneWhoWillBeClosedByDoorAndKey: TZone;
            lateinit var doorZone: TZone;
            lateinit var zonesAfterDoorZone: List<TZone>;


            //Select a zone when you can put a door.
            do {
                doorZone = zones.filter { isZoneAvailable(it) }.random()
                val distanceMax = distanceFromStartMap[doorZone]!!
                zonesAfterDoorZone = doorZone.connected.filter { distanceFromStartMap[it]!! > distanceMax && isZoneAvailable(it as TZone) } as List<TZone>
            } while (zonesAfterDoorZone.isEmpty() && !zones.filter { isZoneAvailable(it) }.isEmpty())

            val distanceMax = distanceFromStartMap[doorZone]!!;
            zoneWhoWillBeClosedByDoorAndKey = zonesAfterDoorZone.random()
            val door =
                    doorZone
                            .content
                            .find { it is DoorObjectZone && it.destination == zoneWhoWillBeClosedByDoorAndKey } as DoorObjectZone

            affectKeyToDoor(
                    door, doorZone, listOfKey
            )
        }
    }


    protected fun affectKeyToDoor(
            door: DoorObjectZone,
            doorZone: TZone,
            listOfKey: MutableList<String>
    ) {
        val availableZoneForKey = availableZonesToPutKeyToAccessZone(doorZone)
        if (!availableZoneForKey.isEmpty()) {
            val keyZone = availableZoneForKey.random() as GeoZone;

            val key = generateKey(listOfKey)

            affectKeyToDoor(door, key)

            keyZone.content.add(key)
            println("key ${key.name} hide $keyZone  to open $doorZone")
        }
    }

    open protected fun availableZonesToPutKeyToAccessZone(doorZone: TZone): Collection<TZone> {
        val distanceMax = distanceFromStartMap[doorZone]!!
        val availableZoneForKey = distanceFromStartMap.filter {
            it.value <= distanceMax && isZoneAvailable(it.key as TZone)
                    && !doorZone.connected.contains(it.key) && doorZone != it.key
        }.keys
        return availableZoneForKey as Set<TZone>
    }

    open protected fun isZoneAvailable(it: TZone): Boolean {
        return true
    }

    protected fun affectKeyToDoor(
            door: DoorObjectZone,
            key: KeyObjectZone
    ) {

        door.key = key
        door.name = keyToDoor.find { it.second == key.name }!!.first
    }


    private fun generateKey(listOfKey: MutableList<String>
    ): KeyObjectZone {
        val name = listOfKey.random()
        listOfKey.remove(name)
        return KeyObjectZone(name)
    }

}

class LabFillerMapLab<T>(
        keyToDoorArray: Array<Array<String>> = doorWithKeyDefault,
        objetDiversArray: Array<String> = objetDiversDefault, board: Board<T>
) : LabFillerExit<T>(keyToDoorArray, objetDiversArray)
        where T : GeoZone, T : ConnectedZone {


    override fun isZoneAvailable(it: T): Boolean {


        var zoneEmpty = it.content.filterIsInstance<KeyObjectZone>().isEmpty()
                && it.content.filterIsInstance<DoorObjectZone>().filter { it.key != null }.isEmpty()
                && this.begin != it

        return zoneEmpty;
    }


}