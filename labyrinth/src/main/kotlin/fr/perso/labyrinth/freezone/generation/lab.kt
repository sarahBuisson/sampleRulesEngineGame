package fr.perso.labyrinth.freezone.generation
import fr.perso.labyrinth.freezone.model.FreeZone
import fr.perso.labyrinth.freezone.model.FreeZoneImpl

fun createCorridor(size: Int): List<FreeZone> {
    val corridor = mutableListOf<FreeZone>(FreeZoneImpl())
    for (i in 0..size) {
        val newZone = FreeZoneImpl()

        newZone.connectTo(corridor.last())
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
            culDeSac.first().connectTo(it)
            lab.addAll(culDeSac);
        }
    }
    return lab
}
