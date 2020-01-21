package fr.perso.labyrinth

import fr.perso.labyrinth.freezone.generation.ObjectZone

interface Zone {}
interface ConnectedZone {
    val connected: List<ConnectedZone>
}

interface NamedZone {
    val name: String
}

interface GeoZone {
    val content: MutableList<ObjectZone>
}
