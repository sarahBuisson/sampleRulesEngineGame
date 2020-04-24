import fr.perso.initPossibleValues
import fr.perso.rules.runBook
import fr.perso.skyscraper.GenerationSkyScraperGrid
import fr.perso.skyscraper.ResolveSkyScraperGrid
import fr.perso.skyscraper.SkyScraperGrid
import org.jeasy.rules.core.RulesImpl
import org.junit.Test

class SkyCraperGenerateIT {


    @Test
    fun generateAndResolveGrid() {
        val gridGenerated = SkyScraperGrid(5, initPossibleValues(5));

        GenerationSkyScraperGrid().run(gridGenerated)
        println(gridGenerated)
        val gridToResolve = SkyScraperGrid(5, initPossibleValues(5));
        gridGenerated.groups.forEachIndexed { index, skyScraperLine ->


            gridToResolve.groups[index].view = skyScraperLine.view

        }

        println(gridToResolve)
        ResolveSkyScraperGrid().run(gridToResolve)
        println(gridToResolve.toStringPossi())
        println(gridToResolve.toStringExport())
        println(gridToResolve)
    }


    @Test
    fun generateAndResolveGrid6() {
        val gridGenerated = SkyScraperGrid(6, initPossibleValues(6));

        runBook(gridGenerated, RulesImpl(setOf(GenerationSkyScraperGrid())))
        println(gridGenerated)
        val gridToResolve = SkyScraperGrid(6, initPossibleValues(6));
        gridGenerated.groups.forEachIndexed { index, skyScraperLine ->


            gridToResolve.groups[index].view = skyScraperLine.view

        }




        println(gridToResolve)
        runBook(gridToResolve, RulesImpl(setOf(ResolveSkyScraperGrid())))
        println(gridToResolve.toStringPossi())
        println(gridToResolve.toStringExport())
        println(gridToResolve)
    }


    @Test
    fun generateAndResolveGrid5() {
        val gridGenerated = SkyScraperGrid(5, initPossibleValues(5));

        runBook(gridGenerated, RulesImpl(setOf(GenerationSkyScraperGrid())))
        println(gridGenerated)
        val gridToResolve = SkyScraperGrid(5, initPossibleValues(5));
        gridGenerated.groups.forEachIndexed { index, skyScraperLine ->


            gridToResolve.groups[index].view = skyScraperLine.view

        }




        println(gridToResolve)
        val resolver = ResolveSkyScraperGrid()
        resolver.run(gridToResolve)
        println(gridToResolve.toStringPossi())
        println(gridToResolve.toStringExport())
        println(gridToResolve)
    }


    @Test
    fun generateAndResolveGrid6Perimeter() {
        val gridSize = 7
        val gridGenerated = SkyScraperGrid(gridSize, initPossibleValues(gridSize));

        GenerationSkyScraperGrid().run(gridGenerated)
        println(gridGenerated)
        val gridToResolve = SkyScraperGrid(gridSize, initPossibleValues(gridSize));
        gridGenerated.groups.forEachIndexed { index, skyScraperLine ->


            gridToResolve.groups[index].view = skyScraperLine.view

        }


        gridGenerated.row1.first().forEachIndexed { index, scase ->


            gridToResolve.row1.first()[index].setValue(scase.getValue())

        }
        gridGenerated.row2.last().forEachIndexed { index, scase ->


            gridToResolve.row2.last()[index].setValue(scase.getValue())

        }
        gridGenerated.column1.first().forEachIndexed { index, scase ->


            gridToResolve.column1.first()[index].setValue(scase.getValue())

        }
        gridGenerated.column2.last().forEachIndexed { index, scase ->


            gridToResolve.column2.last()[index].setValue(scase.getValue())

        }


        println(gridToResolve)
        runBook(gridToResolve, RulesImpl(setOf(ResolveSkyScraperGrid())))
        println(gridToResolve.toStringPossi())
        println(gridToResolve.toStringExport())
        println(gridToResolve)
    }
}
