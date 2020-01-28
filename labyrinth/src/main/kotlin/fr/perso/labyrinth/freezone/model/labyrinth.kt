package fr.perso.labyrinth.freezone.model

import fr.perso.labyrinth.ConnectedZone
import fr.perso.labyrinth.GeoZone
import fr.perso.labyrinth.NamedZone

var index = 0

interface FreeZone : NamedZone, ConnectedZone, GeoZone {
    override val name: String
    override var connected: List<out FreeZone>
    override var content: MutableList<ObjectZone>
}


data class FreeZoneImpl(
        override val name: String = "" + (index++),
        override var connected: List<out FreeZone> = mutableListOf(),
        override var content: MutableList<ObjectZone> = mutableListOf<ObjectZone>()


) : FreeZone {

    override fun toString(): String {
        return name + "(" + connected.map { it.name } + ")" + content.map { it.name } + ""
    }

    override fun connectTo(other: ConnectedZone) {
        other as FreeZone
        (this.connected as MutableList<FreeZone>).add(other)
        (other.connected as MutableList<FreeZone>).add(this)
    }

    override fun unconnectTo(other: ConnectedZone) {
        other as FreeZone
        (this.connected as MutableList<FreeZone>).remove(other)
        (other.connected as MutableList<FreeZone>).remove(this)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

}