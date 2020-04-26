package fr.perso.labyrinth.board.algorithm.composite

import fr.perso.labyrinth.ConnectedZone
import fr.perso.labyrinth.GeoZone
import fr.perso.labyrinth.board.Board
import fr.perso.labyrinth.board.BoardZone
import fr.perso.labyrinth.board.BoardZoneImpl
import fr.perso.labyrinth.board.algorithm.chooseStartExit
import fr.perso.labyrinth.board.algorithm.drawLab
import fr.perso.labyrinth.freezone.gameplay.Partie
import fr.perso.labyrinth.freezone.gameplay.Player
import fr.perso.labyrinth.freezone.generation.*
import fr.perso.labyrinth.freezone.model.ObjectZone

class CompositeZone(x: Int, y: Int) : GeoZone, ConnectedZone, BoardZone, BoardZoneImpl(x, y) {

    override val content: MutableList<ObjectZone> = mutableListOf<ObjectZone>()

}

class LevelBoard<T : Any>(width: Int, height: Int, factory: (x: Int, y: Int, board: Board<T>) -> T) : Board<T>(width, height, factory) {
    lateinit var start: T
    lateinit var exit: T
}

fun generateComposite(size: Int): LevelBoard<CompositeZone> {
    val factory = { x: Int, y: Int, b: Board<CompositeZone> ->
        CompositeZone(
                x,
                y
        )
    }
    val board = LevelBoard<CompositeZone>(
            10, 10, factory
    )
    //
    drawLab(board)
    chooseStartExit(board)
    var doorWithKey = ('A'..'Z').map { arrayOf("" + it, "" + it.toLowerCase()) }.toTypedArray()


    LabFiller<CompositeZone>(doorWithKey)
            .init(board.toList(), board.start, board.exit, 10, 0)
            .fillLab()
    return board
}


fun generateCompositeMapLabWithKey(size: Int): LevelBoard<CompositeZone> {
    val factory = { x: Int, y: Int, b: Board<CompositeZone> ->
        CompositeZone(
                x,
                y
        )
    }
    val board = LevelBoard<CompositeZone>(
            10, 10, factory
    )
    //When
    drawLab(board)
    chooseStartExit(board)

    var doorWithKey = ('A'..'Z').map { arrayOf("" + it, "" + it.toLowerCase()) }.toTypedArray()


    LabFillerMapLab<CompositeZone>(doorWithKey, board = board)
            .init(board.toList(), board.start,board.exit,10, 0)
            .fillLab()
    return board
}


fun initPartieComposite(size: Int = 5): Partie<LevelBoard<CompositeZone>> {
    var lab = generateComposite(size)
    return Partie(Player(lab.start), lab)
}

