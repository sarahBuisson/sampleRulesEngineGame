package fr.perso.labyrinth.freezone.model

import fr.perso.labyrinth.ConnectedZone


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
