package fr.perso.sudoku.service

import fr.perso.initPossibleValues
import fr.perso.sudoku.*
fun <Type> resolveGrid(
        grid: SudokuGrid<Type>,
        resolver: ResolveSudokuGrid<Type> = ResolveSudokuGridMedium<Type>()
): SudokuGrid<Type> {

    resolver.run(grid)
    return grid

}


fun generateCleanedGrid(width: Int, height: Int = width, numberIfRemainingCases: Int = height * width * height / 2): SudokuGrid<Int> {

    var grid = generateFullSudoku(SudokuGrid(width, height, initPossibleValues(width * height)))
    var gridClean = generateCleanedSudoku(grid, numberIfRemainingCases)
    return gridClean;
}