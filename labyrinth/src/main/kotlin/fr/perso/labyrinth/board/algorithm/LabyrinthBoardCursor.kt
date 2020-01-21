package fr.perso.labyrinth.board.algorithm

import fr.perso.labyrinth.board.Board
import fr.perso.labyrinth.board.Direction
import fr.perso.labyrinth.board.PointImpl
import org.jeasy.rules.api.Condition
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.BasicRule
import org.jeasy.rules.core.InferenceRulesEngine


/**
 * the idea is to draw a path randomly by doing action like walking and turning, and that way drawing some sort of path
 */

enum class ObjectType {
    ciseau, clefA, clefB, carteMagnetique, argent, pomme,
    echasseSauteuse,
    echelle, potionGeante, masqueDePlonger, masqueLoup, masqueSorciere, capeSuperMan


}

class ObjectInv(val type: ObjectType)


data class Partie(
        var cursor: Cursor,
        var board: Board<ZoneOfCoridor>, var currentUsedObject: ObjectInv? = null
)


class Cursor(var position: PointImpl, var direction: Direction = Direction.TOP) {
    var distance = 0
}

open class RandomRunRule<P>(protected var proba: Double = 1.0) : BasicRule<P>() {

    override fun evaluate(facts: P): Boolean = (1..10).random() < proba * 10

}

open class Jump : Move {

    override fun evaluate(partie: Partie): Boolean {
        println("walk")
        val newPosition = newPosition(partie, partie.cursor.direction.times(2))
        return super.evaluate(partie) && ((partie.board.get(newPosition)?.haveBeenVisited ?: 0) <= 0)
    }

    constructor() : super() {
        proba = 0.5
    }

    open override fun execute(partie: Partie) {
        super.execute(partie)
        println("fr.perso.labyrinth.board.algorithm.Jump")
        val newPPosition = newPosition(partie, partie.cursor.direction.times(2))
        val oldPPosition = partie.cursor.position

        val newPosition = partie.board.get(newPPosition)!!
        val oldPosition = partie.board.get(oldPPosition)!!

        newPosition.haveBeenVisited = partie.cursor.distance;
        newPosition.connectedZoneAndWayToGo.put(oldPosition, Condition.TRUE as Condition<Partie>)
        oldPosition.connectedZoneAndWayToGo.put(newPosition, Condition.TRUE as Condition<Partie>)

        partie.cursor.position = newPPosition
        partie.cursor.distance++;
    }

    protected fun newPosition(partie: Partie) = partie.cursor.position.add(partie.cursor.direction.times(1))

}

open class Move() : RandomRunRule<Partie>() {

    open override fun execute(partie: Partie) {
        super.execute(partie)
        println("move")
        val newPPosition = newPosition(partie, partie.cursor.direction.times(1))
        val oldPPosition = partie.cursor.position

        val newPosition = partie.board.get(newPPosition)!!
        val oldPosition = partie.board.get(oldPPosition)!!

        newPosition.haveBeenVisited = partie.cursor.distance;
        newPosition.connectedZoneAndWayToGo.put(oldPosition, Condition.TRUE as Condition<Partie>)
        oldPosition.connectedZoneAndWayToGo.put(newPosition, Condition.TRUE as Condition<Partie>)

        partie.cursor.position = newPPosition
        partie.cursor.distance++;
    }

    protected fun newPosition(partie: Partie, vector: PointImpl) = partie.cursor.position.add(vector)

}

open class Turn : RandomRunRule<Partie>() {

    override fun execute(partie: Partie) {
        println("fr.perso.labyrinth.board.algorithm.Turn")
        partie.cursor.direction = Direction.values().random()
    }


}

class MoveWithoutCross : Move() {
    override fun evaluate(partie: Partie): Boolean {
        println("walk")
        val newPosition = newPosition(partie, partie.cursor.direction.times(1))
        return super.evaluate(partie) && ((partie.board.get(newPosition)?.haveBeenVisited ?: 0) <= 0)
    }
}

fun <T> runBook(fact: T, rules: Rules<T>) {
    InferenceRulesEngine<T>().fire(rules, fact)
}

fun main() {

    val factory = { x: Int, y: Int, b: Board<ZoneOfCoridor> ->
        ZoneOfCoridor(
                x,
                y
        )
    }
    val b = Board<ZoneOfCoridor>(
            10, 10, factory
    )

    val partie =
            Partie(
                    Cursor(
                            PointImpl(
                                    0,
                                    0
                            )
                    ), b
            )
    runBook(
            partie,
            Rules(setOf(MoveWithoutCross(), Turn()))
    )

    println(b)
    println(partie)
}
