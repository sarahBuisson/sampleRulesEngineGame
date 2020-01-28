import fr.perso.labyrinth.board.*
import fr.perso.labyrinth.board.algorithm.*
import fr.perso.labyrinth.board.algorithm.composite.CompositeZone
import fr.perso.labyrinth.board.algorithm.composite.generateComposite
import fr.perso.labyrinth.board.algorithm.composite.generateCompositeMapLabWithKey
import fr.perso.labyrinth.freezone.model.DoorObjectZone
import fr.perso.labyrinth.freezone.model.KeyObjectZone
import junit.framework.Assert.assertEquals
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
        chooseStartExit(board)
        //Then
        println(labyrinthTreeToString(board))
        println("----")
        val distance = distance(board.start, board)
        println(labyrinthTreeToString(board, { distance.get(it) }))
        println("----")
        val complexite = complexiteMap(board.start, board)
        println(labyrinthTreeToString(board, { complexite.get(it) }))
        println("----")
        val coridorSize = coridorSizeDistanceMap( board)
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


    @Test
    fun shouldGenerateLabWithKey() {

        val factory = { x: Int, y: Int, b: Board<CompositeZone> ->
            CompositeZone(
                    x,
                    y
            )
        }
        val board = generateCompositeMapLabWithKey(10)
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



    @Test
    fun should_follow_corridor(){

        val factory = { x: Int, y: Int, b: Board<CompositeZone> ->
            CompositeZone(
                    x,
                    y
            )
        }
        val board = Board<CompositeZone>(
                10, 1, factory
        )
        drawLab(board)
        println(board)
        assertEquals(followCorridor(board.get(0,0)!!).size,10)
    }

}
