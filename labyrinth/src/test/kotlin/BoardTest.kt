import fr.perso.labyrinth.board.*
import fr.perso.labyrinth.board.algorithm.*
import org.jeasy.rules.api.Rules
import org.junit.Test

class BoardTest {


    @Test
    fun shouldGenerateBoard() {


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
            Partie(Cursor(PointImpl(3, 3)), b)
        runBook(
            partie,
            Rules(
                setOf(
                    MoveWithoutCross(),
                    Turn(),
                    Jump()
                )
            )
        )

        println(b)
        println(partie.cursor.position)


    }


    @Test
    fun shouldGenerateCoridorSimple() {


        val factory = { x: Int, y: Int, b: Board<ZoneOfCoridor> ->
            ZoneOfCoridor(
                x,
                y
            )
        }
        val b = Board<ZoneOfCoridor>(
            10, 10, factory
        )

        val coridor =
            drawDirectCoridorH(
                b,
                PointImpl(4, 2),
                PointImpl(9, 5)
            )

        coridor.forEachIndexed { index: Int, ZoneOfCoridor: ZoneOfCoridor -> ZoneOfCoridor.haveBeenVisited = index+1; }
        println(b.toString { "" + it.haveBeenVisited })
        println(labyrinthToString(b, listOf(coridor)))
    }

    @Test
    fun shouldGenerateCoridorComplex() {


        val factory = { x: Int, y: Int, b: Board<ZoneOfCoridor> ->
            ZoneOfCoridor(
                x,
                y
            )
        }
        val b = Board<ZoneOfCoridor>(
            10, 10, factory
        )

        val coridor:List<ZoneOfCoridor> =
            drawComplexCoridorH(
                b,
                PointImpl(4, 2),
                PointImpl(9, 5)
            )

        coridor.forEachIndexed { index: Int, ZoneOfCoridor: ZoneOfCoridor -> ZoneOfCoridor.haveBeenVisited = index + 1 }
        println(b.toString {
            if (it.haveBeenVisited == 0) {
                " "
            } else {
                "" + it.haveBeenVisited
            }
        })

        println(labyrinthToString(b, listOf(coridor)))

    }

    @Test
    fun shouldGenerateCoridorWith1Complexity() {


        val factory = { x: Int, y: Int, b: Board<ZoneOfCoridor> ->
            ZoneOfCoridor(
                x,
                y
            )
        }
        val b = Board<ZoneOfCoridor>(
            10, 10, factory
        )

        val coridor =
            drawDirectCoridorH(
                b,
                PointImpl(4, 2),
                PointImpl(9, 5)
            )


        coridor.forEachIndexed { index: Int, ZoneOfCoridor: ZoneOfCoridor -> ZoneOfCoridor.haveBeenVisited = index+1; }
        println(b.toString { "" + it.haveBeenVisited })

        val coridor2 = addComplexityToCoridorAtElement(b, coridor, b.get(9, 2)!!)

        coridor2.forEachIndexed { index: Int, ZoneOfCoridor: ZoneOfCoridor -> ZoneOfCoridor.haveBeenVisited = index+1; }
        println(b.toString {
            if (it.haveBeenVisited == 0) {
                " "
            } else {
                "" + it.haveBeenVisited
            }
        })

        println(labyrinthToString(b, listOf(coridor2)))

    }




    @Test
    fun shouldGenerateLabyrinth() {
        val factory = { x: Int, y: Int, b: Board<ZoneOfCoridor> ->
            ZoneOfCoridor(
                x,
                y
            )
        }
        val b = Board<ZoneOfCoridor>(
            10, 10, factory
        )
       val coridors= drawLabyrinth(b)
        println("full lab")
        println(labyrinthToString(b, coridors))

        println("only solution ")
        println(labyrinthToString(b, listOf(coridors.first())))
        println("only dead end ")
        println(
            labyrinthToString(
                b,
                coridors.minus(coridors.first()) as List<List<ZoneOfCoridor>>
            )
        )



    }

}
