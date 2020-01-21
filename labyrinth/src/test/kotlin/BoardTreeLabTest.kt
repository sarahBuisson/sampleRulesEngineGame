import fr.perso.labyrinth.board.*
import fr.perso.labyrinth.board.algorithm.*
import fr.perso.labyrinth.board.algorithm.composite.CompositeZone
import fr.perso.labyrinth.board.algorithm.composite.generateComposite
import fr.perso.labyrinth.freezone.generation.DoorObjectZone
import fr.perso.labyrinth.freezone.generation.KeyObjectZone
import fr.perso.labyrinth.freezone.generation.LabFiller
import org.junit.Test

class BoardTreeLabTest {


    @Test
    fun shouldGenerateTreeLab() {

        val factory = { x: Int, y: Int, b: Board<BoardZone> ->
            BoardZoneImpl(
                    x,
                    y
            )
        }
        val board = Board<BoardZone>(
                10, 10, factory
        )
        //When
        drawLab(board)
        chooseExit(board)
        //Then
        println(labyrinthTreeToString(board))
        println("----")
        val distance = distance(board.start, board)
        println(labyrinthTreeToString(board, { distance.get(it) }))
        println("----")
        val complexite = complexite(board.start, board)
        println(labyrinthTreeToString(board, { complexite.get(it) }))
        println("----")
        val coridorSize = coridorSize(board.start, board)
        println(labyrinthTreeToString(board, { coridorSize.get(it) }))
    }


    @Test
    fun shouldGenerateCompositeBoardLab() {

        val factory = { x: Int, y: Int, b: Board<CompositeZone> ->
            CompositeZone(
                    x,
                    y
            )
        }
        val board = generateComposite(10)
        //Then
        println(labyrinthTreeToString(board))
        println("----")


        val compositeZoneName: (CompositeZone) -> Any? =
                {
                    val objs = it.content.filter { it is KeyObjectZone }

                    if (objs.isNotEmpty())
                        objs.first().name
                    else
                        defaultZoneName(it)


                }
        val compositeDoorName: (Direction, CompositeZone) -> Any? = { d, zone ->
            val door = zone.content.filterIsInstance(DoorObjectZone::class.java)
                    .filter { it.destination == zone.connections.get(d) }.firstOrNull()
            if (door != null && door.key != null)
                door.name
            else
                defaultDoorName(d, zone)
        }
        println(
                labyrinthTreeToString(
                        board,
                        compositeZoneName,
                        compositeDoorName
                )
        )
    }

}
