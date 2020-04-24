import fr.perso.initPossibleValues
import fr.perso.rules.runBook
import fr.perso.skyscraper.GenerationSkyScraperGrid
import fr.perso.skyscraper.ResolveSkyScraperGrid
import fr.perso.skyscraper.SkyScraperGrid
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl
import org.junit.Test

class SkyscraperIT {


    @Test
    fun shouldResove33() {

        val content = "0 0 0 0 0 0 0 0 0\n" +
                "3 2 1\n" +
                "1 2 2\n" +
                "3 2 1\n" +
                "1 2 2\n"

        val grid = SkyScraperGrid(3, initPossibleValues(3));
        println(grid)
        grid.fill(content)
        println(grid)

        println(grid.columns.first().first().getPossibles())
        println("---")
        runBook(grid, RulesImpl(setOf(ResolveSkyScraperGrid())))
        println(grid)
        println(grid.toStringPossi())
        println(grid.columns.first().first().getPossibles())


    }


    @Test
    fun shouldResove55() {

        val content = "00000\n" +
                "00000\n" +
                "00000\n" +
                "00000\n" +
                "00000\n" +
                "32341\n" +
                "33212\n" +
                "32124\n" +
                "13432"

        val grid = SkyScraperGrid(5, initPossibleValues(5));
        println(grid)
        grid.fill(content)
        println(grid)

        println(grid.columns.first().first().getPossibles())
        println("---")
        runBook(grid, RulesImpl(setOf(ResolveSkyScraperGrid())))
        println(grid)
        println(grid.toStringPossi())
        println(grid.columns.first().first().getPossibles())

    }

    @Test
    fun shouldResove552() {

        val content = "00000\n" +
                "00000\n" +
                "00000\n" +
                "00000\n" +
                "00000\n" +
                "23212\n" +
                "32241\n" +
                "31223\n" +
                "24341"

        val grid = SkyScraperGrid(5, initPossibleValues(5));
        println(grid)
        grid.fill(content)
        println(grid)

        println(grid.columns.first().first().getPossibles())
        println("---")
        runBook(grid, RulesImpl(setOf(ResolveSkyScraperGrid())))
        println(grid)
        println(grid.toStringPossi())
        println(grid.columns.first().first().getPossibles())

    }

    @Test
    fun shouldResove553() {

        val content = "00000\n" +
                "00000\n" +
                "00000\n" +
                "00000\n" +
                "00000\n" +
                "42122\n" +
                "12532\n" +
                "33421\n" +
                "32122"

        val grid = SkyScraperGrid(5, initPossibleValues(5));
        println(grid)
        grid.fill(content)
        println(grid)

        println(grid.columns.first().first().getPossibles())
        println("---")
        runBook(grid, RulesImpl(setOf(ResolveSkyScraperGrid())))
        println(grid)
        println(grid.toStringPossi())
        println(grid.columns.first().first().getPossibles())

    }


}
