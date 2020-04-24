package fr.perso.sudoku

import fr.perso.SCasePossible
import fr.perso.rules.*
import org.jeasy.rules.api.Rule
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl


open class ResolveSudokuGridEasy<Type> : ResolveSudokuGrid<Type> {

    constructor() {
        // this.addGroupeRule(   RemovePossibleLockFromTheGroup<Type, Iterable<SCasePossible<Type>>>(2))
    }

}


open class ResolveSudokuGridMedium<Type> : ResolveSudokuGrid<Type> {

    constructor() {
        this.addGroupeRule(RemovePossibleLockFromTheGroup<Type, Iterable<SCasePossible<Type>>>(4))
    }

}

open class ResolveSudokuGridDifficult<Type> : ResolveSudokuGrid<Type> {

    constructor() {
        val hypothesisRule: Rule<SudokuGrid<Type>> = UseRandomHypothesis(
                this::isGridPossibleValid,
                this::execute
        )
        this.addGridRule(hypothesisRule)
        this.addGroupeRule(RemovePossibleLockFromTheGroup<Type, Iterable<SCasePossible<Type>>>())
    }

}


open class ResolveSudokuGrid<Type>() :
        ResolveGrid<Type, Iterable<SCasePossible<Type>>, SudokuGrid<Type>>() {
    override var caseRules: Rules<SCasePossible<Type>> = RulesImpl(setOf(OnePossibiliteSet<Type>()))
    override var groupeRules: Rules<out Iterable<SCasePossible<Type>>> = RulesImpl(
            setOf(
                    RemoveSureValueFromTheRestOfTheGroup<Type, Iterable<SCasePossible<Type>>>()
            )
    )
    override var gridRules: Rules<out SudokuGrid<Type>> = RulesImpl<SudokuGrid<Type>>()

    override fun isGridPossibleValid(grid: SudokuGrid<Type>): Boolean {

        val errors = errorInTheGrid(grid)


        return errors.isEmpty()

    }

    override fun errorInTheGrid(grid: SudokuGrid<Type>): List<Any> {
        val errors = grid.groups.map { group ->

            val respectUnicity = respectUnicity(group, grid.possibles)
            val respectPossiblePresence = respectPossiblePresence(group, grid.possibles)
            val errors = mutableListOf<Any>()
            errors.addAll(respectUnicity)
            errors.addAll(respectPossiblePresence)
            errors

        }.flatten()
        return errors
    }
}

class ResolveSudokuBuilder




