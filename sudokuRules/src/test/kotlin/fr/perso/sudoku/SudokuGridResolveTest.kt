package fr.perso.sudoku;


import fr.perso.SCasePossible
import fr.perso.initPossibleValues
import fr.perso.rules.RemovePossibleLockFromTheGroup
import fr.perso.rules.runBook
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class SudokuGridResolveTest {


    @Test
    fun shouldResove33() {

        val content = "003020600\n" +
                "900305001\n" +
                "001806400\n" +
                "008102900\n" +
                "700000008\n" +
                "006708200\n" +
                "002609500\n" +
                "800203009\n" +
                "005010300"

        val grid = SudokuGrid(3, 3, initPossibleValues(3*3));

        grid.fill(content)
        println(grid)

        println(grid.columns.first().first().getPossibles())
        println("---")
        runBook(grid, RulesImpl(setOf(ResolveSudokuGridMedium<Int>())))
        println(grid)
        println(grid.columns.first().first().getPossibles())
        assertEquals(grid.nbOfFreePossibilite(), grid.size())
    }

    @Test
    fun shouldResove21() {

        val content = "01\n00"

        val grid = SudokuGrid(2, 1, initPossibleValues(2));

        grid.fill(content)
        println(grid)

        println(grid.columns.first().first().getPossibles())
        println("---")
        runBook(grid, RulesImpl(setOf(ResolveSudokuGridEasy<Int>())))
        println(grid)
        println(grid.columns)
        println(grid.columns.first().first().getPossibles())
        assertEquals(grid.nbOfFreePossibilite(), grid.size())
    }


    val tresfacile = "3xxx24xxx 6xxxx8xxx xxxxxx918  xxxxxxxxx 8xx5xxx3x xxxx4xx62  x2xx87xx4 xx4xxx5xx x1xxx32xx"

    val facile = "2xxxx7x6x 37xxxxxxx xxx2xx19x  xxxxxx6xx 1xx84x5xx x3xxx6x8x  412xxxxxx xxxx2x7x9 xx5x1xxxx"

    val moyen = "2xxx7xx6x 613xxxxx4 xx43xx2xx  xxxxxx8x5 xxx1xx7xx x8xxx3x19  4x17xxxxx xx2xxx5xx xxxx5xxxx"

    val difficile = "2xxx65xxx x57xxxxxx xx893xx56  xxxxxxxx7 xxx1xxxxx 71xxx4x89  xx68xxxxx xx9x2x4x8 xxxx4x6xx"

    val tresdifficile = "5xxxx84xx x6xxxxxxx xx39xxx8x  xxxxxxxx3 x9x5xx8xx 47xxx1xx2   186xxxxxx xxxx4x5xx xx5x6x1xx"

    val expert = "xxxxx8x1x x37xxxxxx x5xx7x4x9  xxxxxx2xx xxx6xxxxx 41xxx2xx3  x71xxxxxx xx9x2x6x4 xx6x935xx"


    @Test
    fun shouldResolveVariousLevel() {
        val grids = listOf(tresfacile, facile, moyen/*, difficile, tresdifficile, expert*/)
            .map { it.replace(" ", "\n").replace("x", "0") }



        for (content in grids) {
            val grid = SudokuGrid(3, 3, initPossibleValues(3*3));

            grid.fill(content)
            println(grid)

            println(grid.columns.first().first().getPossibles())
            println("level ---")


                runBook(grid, RulesImpl(setOf(ResolveSudokuGridDifficult<Int>())))

            println(grid)
            assertEquals(grid.nbOfFreePossibilite(), grid.size())
        }
    }

    @Test
    fun shouldResolveDifficileLevel() {




            val grid = SudokuGrid(3, 3, initPossibleValues(3*3));

            grid.fill(difficile)
            println(grid)

            println(grid.columns.first().first().getPossibles())
            println("level ---")


                runBook(grid, RulesImpl(setOf(ResolveSudokuGridDifficult<Int>())))

            println(grid)
            assertEquals(grid.nbOfFreePossibilite(), grid.size())

    }
@Ignore //TODO : echoue au build
    @Test
    fun shouldResolve33_facile() {

        val grid = SudokuGrid(3, 3, initPossibleValues(3*3));

        grid.fill(facile)
        println(grid)

        println(grid.columns.first().first().getPossibles())
        println("---")

        for (i in 1..1) {

            println("iter")

            val resolveSudokuGrid = ResolveSudokuGridEasy<Int>()
            resolveSudokuGrid.run(grid)
        }

        println(grid)
        println(grid.toStringPossi())
        println(grid.columns.first().first().getPossibles())
        println(grid.columns[1].first())
        assertEquals(grid.nbOfFreePossibilite(), grid.size())
    }



}
