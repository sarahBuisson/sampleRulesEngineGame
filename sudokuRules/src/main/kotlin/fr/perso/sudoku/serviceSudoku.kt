package fr.perso.sudoku

fun <Type> resolveGrid(
        grid: SudokuGrid<Type>,
        resolver: ResolveSudokuGrid<Type> = ResolveSudokuGridMedium<Type>()
): SudokuGrid<Type> {

    resolver.run(grid)
    return grid

}
