package fr.perso.labyrinth.freezone.gameplay

import fr.perso.labyrinth.GeoZone
import fr.perso.labyrinth.freezone.generation.*
import fr.perso.labyrinth.freezone.model.*
import org.jeasy.rules.api.Rule
import org.jeasy.rules.core.RulesImpl
import org.jeasy.rules.core.DefaultRulesEngine
import org.jeasy.rules.core.LambdaRule

data class Player(
        var location: GeoZone,
        val inventory: MutableList<ObjectZone> = mutableListOf<ObjectZone>(),
        var selected: ObjectZone? = null
)


class Partie<LevelType>(val player: Player, val level:LevelType)


fun initLab(size: Int = 5): Partie<*> {
    val lab = createLab(size)
    LabFiller<FreeZone>()
            .init(lab, lab.first(),lab.last(), size, 0)
            .fillLab();
    return Partie(Player(lab.first()), lab)
}

fun initPartie(size: Int = 5): Partie<List<FreeZone>> {
    val lab = createLab(size)
    LabFiller<FreeZone>()
            .init(lab, lab.first(), lab.last(), size, size)
            .fillLab();
    return Partie(Player(lab.first()), lab)
}


fun initPartieExit(size: Int = 5): Partie<List<FreeZone>> {
    val lab = createLab(size)
    val keyToDoorArray= mutableListOf<Array<String>>()
    val objetDiversArray= mutableListOf<String>()

    for (i in 0..size){

        keyToDoorArray.add(arrayOf(""+i.toChar(),""+i.toChar().toUpperCase()))
        objetDiversArray.add(""+(size+i).toChar())

    }
    LabFillerExit<FreeZone>(keyToDoorArray.toTypedArray(), objetDiversArray.toTypedArray())
            .init(lab, lab.first(), lab.random(), size,size/2).fillLab();
    return Partie(Player(lab.first()), lab)
}



class Interaction<Qui, Quoi, Comment, Univers>(val qui: Qui, val quoi: Quoi, val comment: Comment, val univers: Univers)


class MoveRule :
        LambdaRule<Interaction<Player, Any, Any, Partie<*>>>(
                { interaction ->
                    interaction.quoi is DoorObjectZone && interaction.quoi.key == null && interaction.qui.location.content.contains(interaction.quoi)
                },
                { interaction ->
                    interaction.qui.location = (interaction.quoi as DoorObjectZone).destination as GeoZone
                })


class MoveClosedDoorRule :
        LambdaRule<Interaction<Player, Any, Any, Partie<*>>>(
                { interaction ->
                    MoveRule::evaluate.call(interaction)
                            && interaction.qui.inventory.contains((interaction.quoi as DoorObjectZone).key)
                },
                { interaction ->
                    MoveRule::execute.call(interaction)
                })


class TakeObjectRule :
        LambdaRule<Interaction<Player, Any, Any, Partie<*>>>(
                { interaction ->
                    interaction.quoi is KeyObjectZone
                },
                { interaction ->
                    interaction.qui.location.content.remove(interaction.quoi as KeyObjectZone)
                    interaction.qui.inventory.add(interaction.quoi as KeyObjectZone)
                })

class ExchangeObjectRule :
        LambdaRule<Interaction<Player, Any, Any, Partie<*>>>(
                { interaction ->
                    interaction.quoi is ExchangeObjectZone &&
                            interaction.qui.selected == interaction.quoi.want
                },
                { interaction ->
                    interaction.qui.inventory.remove((interaction.quoi as ExchangeObjectZone).want)
                    interaction.qui.inventory.add((interaction.quoi as ExchangeObjectZone).give)
                    interaction.qui.selected = null;
                    interaction.quoi.name = "thanks !"
                })


val ruleBook = RulesImpl(setOf(MoveRule(), MoveClosedDoorRule(), TakeObjectRule(), ExchangeObjectRule()))


fun playerInteractWith(partie: Partie, obj: ObjectZone): Partie {
    DefaultRulesEngine<Interaction<Player, Any, Any, Partie>>().fire(
            ruleBook,
            Interaction(partie.player, obj as Any, "" as Any, partie)
    )
    return Partie(partie.player, partie.level)

}
