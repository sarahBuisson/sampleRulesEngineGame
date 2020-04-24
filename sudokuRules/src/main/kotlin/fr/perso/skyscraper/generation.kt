package fr.perso.skyscraper

import fr.perso.SCasePossible
import fr.perso.rules.*
import org.jeasy.rules.api.Rule
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl
import org.jeasy.rules.core.BasicRule

class GenerationView(
) : BasicRule<SkyScraperLine>() {


    override fun evaluate(facts: SkyScraperLine): Boolean {
        return facts.all { it.getValue() != null }

    }

    override fun execute(group: SkyScraperLine) {
        group.view = countViewableCase(group.map { it.getValue() })

    }

}


class GenerationSkyScraperGrid() :
        ResolveGrid<Int, SkyScraperLine, SkyScraperGrid>() {

    override var gridRules: Rules<out SkyScraperGrid> = RulesImpl(setOf(FillRandomlyOneCase(this::isGridPossibleValid, this::execute)))
    open override var caseRules: Rules<SCasePossible<Int>> = RulesImpl(setOf(OnePossibiliteSet<Int>()))
    open override var groupeRules: Rules<out SkyScraperLine> = RulesImpl(
            setOf<Rule<SkyScraperLine>>(
                    RemoveSureValueFromTheRestOfTheGroup<Int, SkyScraperLine>(),
                    RemovePossibleLockFromTheGroup<Int, SkyScraperLine>(2),
                    GenerationView()

            )
    )

    override fun errorInTheGrid(grid: SkyScraperGrid): List<Any> {
        return grid.groups.map { group ->

            val respectUnicity = respectUnicity(group, grid.possibles)
            val respectPossiblePresence = respectPossiblePresence(group, grid.possibles)
            val errors = mutableListOf<Any>()
            errors.addAll(respectUnicity)
            errors.addAll(respectPossiblePresence)

            errors

        }.flatten()
    }

    open override fun isGridPossibleValid(grid: SkyScraperGrid): Boolean {

        val errors = errorInTheGrid(grid)

        return errors.isEmpty()

    }
}


