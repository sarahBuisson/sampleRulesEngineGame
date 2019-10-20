import org.junit.Test

class BoardTest {


    @Test
    fun samplePartie() {


        val partie = Partie(4);


        runPartieAuto(partie)
        println(someoneIsWinning(partie))
        println(someoneIsWinning(partie)!!.horses.first().events.size)
        println(someoneIsWinning(partie)!!.horses.first().events)

    }


    @Test
    fun partieOneOne() {


        val board = Board(1)
        val partie = Partie(listOf(Player(startPosition = board.road.first(), size = 1)), board)

        runPartieAuto(partie)
        println("winner ")
        print(someoneIsWinning(partie))

    }



}
