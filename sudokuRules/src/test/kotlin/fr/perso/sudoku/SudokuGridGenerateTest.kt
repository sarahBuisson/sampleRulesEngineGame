package fr.perso.sudoku;


import fr.perso.initPossibleValues
import fr.perso.sudoku.service.generateCleanedGrid
import fr.perso.sudoku.service.resolveGrid
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

class SudokuGridGenerateTest {


    @Test
    fun shouldGenerateFull33() {

        var grid = generateFullSudoku(SudokuGrid(3, 3, initPossibleValues(3 * 3)), ResolveSudokuGridEasy())
        println(grid)
        assertTrue(ResolveSudokuGrid<Int>().isGridPossibleValid(grid))
    }


    @Test
    fun shouldGenerateFull9Facile() {

        var grid = generateFullSudoku(SudokuGrid(3, 3, initPossibleValues(3 * 3)))
        println(grid)
        assertTrue(ResolveSudokuGrid<Int>().isGridPossibleValid(grid))
    }

    @Test
    fun shouldGenerateAndCleanFull33DifficultyVarious() {

        var grid = generateFullSudoku(SudokuGrid(3, 3, initPossibleValues(3 * 3)))
        println(grid)
        var gridClean = generateCleanedSudoku(grid, 50)
        println(gridClean)
        gridClean = generateCleanedSudoku(gridClean, 20)
        println(gridClean)
        gridClean = generateCleanedSudoku(gridClean, 8)
        println(gridClean)
        var resolved = resolveGrid(gridClean)

        println("resolvedGrid")
        println(resolved)
    }


    @Test
    fun shouldGenerateAndCleanFull33Difficulty30() {

        var grid = generateFullSudoku(SudokuGrid(3, 3, initPossibleValues(3 * 3)))
        println(grid)
        var gridClean = generateCleanedSudoku(grid, 30)
        println(gridClean)
        var resolved = resolveGrid(gridClean)

        println("resolvedGrid")
        println(resolved)
    }

    @Test
    fun shouldGenerateAndCleanFull33Easy() {

    var gridClean = generateCleanedGrid(3, 3, 30)
    println(gridClean)
    var resolved = resolveGrid(gridClean)

    println("resolvedGrid")
    println(resolved)
    }

@Ignore// sometime fail
    @Test
    fun shouldGenerateAndCleanFull33Difficulty15() {

        var grid = generateFullSudoku(SudokuGrid(3, 3, initPossibleValues(3 * 3)))
        println(grid)
        assertTrue(ResolveSudokuGrid<Int>().isGridPossibleValid(grid))

        var gridClean = generateCleanedSudoku(grid, 15)
        println(gridClean)
        var resolved = resolveGrid(gridClean)

        println("resolvedGrid")
        println(resolved)
    }
@Ignore//Take too long
    @Test
    fun shouldGenerateAndCleanFull33Difficulty9() {

        var grid = generateFullSudoku(SudokuGrid(3, 3, initPossibleValues(3 * 3)))
        println(grid)

        val resolverSudokuGrid = ResolveSudokuGridDifficult<Int>()

        var gridClean = generateCleanedSudoku(grid, 9, resolverSudokuGrid)
        println(gridClean)

        var resolved = resolverSudokuGrid.run(gridClean)

        println("resolvedGrid")
        println(resolved)

    }


}
