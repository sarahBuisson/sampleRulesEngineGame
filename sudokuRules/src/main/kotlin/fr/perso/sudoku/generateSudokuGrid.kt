package fr.perso.sudoku

import fr.perso.SCasePossible
import fr.perso.rules.*
import fr.perso.skyscraper.SkyScraperGrid
import fr.perso.sudoku.service.resolveGrid

fun <Type> generateFullSudoku(grid: SudokuGrid<Type>, resolver: ResolveSudokuGrid<Type> = ResolveSudokuGridMedium()): SudokuGrid<Type> {
    val newL = ArrayList(grid.possibles).shuffled()

    for (i in 0..grid.possibles.size - 1) {
        val possible = newL[i]
        grid.get(0, 1).setValue(possible)

    }
    val generator = GenerationSudokuGridFull<Type>()

    generator.addAllRules(resolver)
    generator.run(grid)
    grid.forEach { it.solution = it.getValue() }

    return resolveGrid(grid)
}


class GenerationSudokuGridFull<Type> :
        ResolveSudokuGrid<Type> {


    constructor() {
        addGroupeRule(RemovePossibleLockFromTheGroup(3))
        addGridRule(FillRandomlyOneCase(this::isGridPossibleValid, this::execute))
    }

    fun isGridPossibleValid(grid: SkyScraperGrid): Boolean {

        val errors = grid.groups.map { group ->

            val respectUnicity = respectUnicity(group, grid.possibles)
            val respectPossiblePresence = respectPossiblePresence(group, grid.possibles)
            //val checkIfViewCountRespected = checkIfViewCountRespected(group, grid.possibles)
            val errors = mutableListOf<Any>()
            errors.addAll(respectUnicity)
            errors.addAll(respectPossiblePresence)
            errors

        }.flatten()


        return errors.isEmpty()

    }
}


class ResolvePartialSudokyGrid<Type>() : ResolveSudokuGrid<Type>() {
    var x: Int = 0
    var y: Int = 0
    override fun isNotResolved(grid: SudokuGrid<Type>) = !isCaseResolved(grid)

    fun isCaseResolved(grid: SudokuGrid<Type>) = grid.get(x, y).getValue() != null

    fun run(grid: SudokuGrid<Type>, x: Int, y: Int) {
        this.x = x
        this.y = y
        super.run(grid)

    }

}

/**
 * generate a sudoky grid with missing case
 */
fun <Type> generateCleanedSudoku(grid: SudokuGrid<Type>,
                                 numberOfRemanningCase: Int,
                                 resolver: ResolveSudokuGrid<Type> = ResolveSudokuGridMedium<Type>()
): SudokuGrid<Type> {
    val caseResolveRule: ResolvePartialSudokyGrid<Type> = ResolvePartialSudokyGrid<Type>()
    caseResolveRule.addAllRules(resolver);
    var gridToUpdate = grid.clone();
    val casesWithValueNeededForResolution = mutableListOf<SCasePossible<Type>>()
    do {

        val filter = grid.filter { it.getValue() != null }
                .filter { !casesWithValueNeededForResolution.contains(it) }

        val caseCleanable = filter.random()

        if (caseCleanable != null) {
            var modifiedGrid = gridToUpdate.clone()
            modifiedGrid.get(caseCleanable.x, caseCleanable.y).resetCase(grid.possibles)
            modifiedGrid.resetPossibles()

            caseResolveRule.run(modifiedGrid, caseCleanable.x,
                    caseCleanable.y)
            if (caseResolveRule.isCaseResolved(modifiedGrid) && modifiedGrid.get(
                            caseCleanable.x,
                            caseCleanable.y
                    ).getValue() != caseCleanable.getValue()
            ) {
                caseResolveRule.isCaseResolved(modifiedGrid);
                println("ERROR, have found anotherValue")
            } else
                if (caseResolveRule.isCaseResolved(modifiedGrid)) {
                    // println("resolved")
                    // println(modifiedGrid)
                    gridToUpdate.get(caseCleanable.x, caseCleanable.y).resetCase(grid.possibles)

                } else {
                    println("add to needed " + casesWithValueNeededForResolution.size)
                    casesWithValueNeededForResolution.add(caseCleanable);
                }
        } else {
            println("caseAlreadyNull")
        }
        val numberOfCaseWithStillAValue = gridToUpdate.count { it.getValue() != null }
        val difficultyNotReached = numberOfCaseWithStillAValue >= numberOfRemanningCase
        val allRemaningCaseAreNeeded = numberOfCaseWithStillAValue <= casesWithValueNeededForResolution.size

        if (allRemaningCaseAreNeeded) {
            println("allRemaningCaseAreNeeded")
        }

    } while (difficultyNotReached && !allRemaningCaseAreNeeded)


    return gridToUpdate;
}

