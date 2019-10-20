package fr.perso.labyrinth.board.algorithm.composite

import fr.perso.labyrinth.ConnectedZone
import fr.perso.labyrinth.GeoZone
import fr.perso.labyrinth.board.Board
import fr.perso.labyrinth.board.BoardZone
import fr.perso.labyrinth.board.BoardZoneImpl
import fr.perso.labyrinth.board.algorithm.chooseExit
import fr.perso.labyrinth.board.algorithm.drawLab
import fr.perso.labyrinth.freezone.gameplay.Partie
import fr.perso.labyrinth.freezone.gameplay.Player
import fr.perso.labyrinth.freezone.generation.FreeZone
import fr.perso.labyrinth.freezone.generation.LabFiller
import fr.perso.labyrinth.freezone.generation.ObjectZone
import fr.perso.labyrinth.freezone.generation.createLab

class CompositeZone(x: Int, y: Int) : GeoZone, ConnectedZone,BoardZone, BoardZoneImpl(x, y) {

    override val content: MutableList<ObjectZone> = mutableListOf<ObjectZone>()

}


fun generateComposite(size: Int): Board<CompositeZone> {
    val factory = { x: Int, y: Int, b: Board<CompositeZone> ->
        CompositeZone(
            x,
            y
        )
    }
    val board = Board<CompositeZone>(
        10, 10, factory
    )
    //When
    drawLab(board)
    chooseExit(board)

    var doorWithKey = ('A'..'Z').map { arrayOf("" + it, "" + it.toLowerCase()) }.toTypedArray()


    LabFiller<CompositeZone>(doorWithKey).fillLab(board.toList(), board.start,10, 0)
    return board
}


fun initPartieComposite(size:Int=5): Partie {
    var lab= generateComposite(size)
    return Partie(Player(lab.start), lab.toList())
}
