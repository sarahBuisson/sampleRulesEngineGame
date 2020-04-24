package fr.perso.skyscraper

import fr.perso.initPossibleValues
import fr.perso.rules.runBook
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl


fun generateEmptyGrid(size: Int = 5): SkyScraperGrid {
    val gridGenerated = SkyScraperGrid(size, initPossibleValues(size));

    runBook(gridGenerated, RulesImpl(setOf(GenerationSkyScraperGrid())))
    println(gridGenerated)
    val gridToResolve = SkyScraperGrid(size, initPossibleValues(size));
    gridGenerated.groups.forEachIndexed { index, skyScraperLine ->


        gridToResolve.groups[index].view = skyScraperLine.view

    }
    return gridToResolve;
}

fun resolveGrid(grid: SkyScraperGrid): SkyScraperGrid {

    runBook(grid, RulesImpl(setOf(ResolveSkyScraperGrid())))
    return grid

}
