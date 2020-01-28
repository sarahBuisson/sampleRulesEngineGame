package fr.perso.labyrinth

import fr.perso.labyrinth.freezone.model.ObjectZone

interface Zone {}
interface ConnectedZone {
    val connected: List<out ConnectedZone>

    fun connectTo(other: ConnectedZone)

    fun unconnectTo(other: ConnectedZone)
}

interface NamedZone {
    val name: String
}

interface GeoZone {
    val content: MutableList<ObjectZone>
}
