package fr.perso.sudoku;

import fr.perso.SCasePossible
import fr.perso.initPossibleValues
import kotlin.test.*

class SudokuGridTest {


    @Test
    fun shouldPrintRow23() {


        val s = SudokuGrid(2, 3, initPossibleValues(6))
        for ((index: Int, square: List<SCasePossible<Int>>) in s.rows.withIndex()) {
            square.forEach { it.setValue(index) };
        }
        println(s.toString())
    }

    @Test
    fun shouldPrintColumn23() {


        val s = SudokuGrid(2, 3, initPossibleValues(6))
        for ((index: Int, square: List<SCasePossible<Int>>) in s.columns.withIndex()) {
            square.forEach { it.setValue(index) };
        }
        println(s.toString())
    }


    @Test
    fun shouldPrintSquare32() {


        val s = SudokuGrid(3, 2, initPossibleValues(6))
        val squares: List<List<SCasePossible<Int>>> = s.squares
        for ((index: Int, square: List<SCasePossible<Int>>) in squares.withIndex()) {
            square.forEach { it.setValue(index) };
        }
        println(s.toString())
    }

    @Test
    fun shouldPrintSquare23() {


        val s = SudokuGrid(2, 3, initPossibleValues(6))
        val squares: List<List<SCasePossible<Int>>> = s.squares
        for ((index: Int, square: List<SCasePossible<Int>>) in squares.withIndex()) {
            square.forEach { it.setValue(index) };
        }
        println(s.toString())
    }

    @Test
    fun shouldPrintSquare33() {


        val s = SudokuGrid(3, 3, initPossibleValues(9))
        val squares: List<List<SCasePossible<Int>>> = s.squares
        for ((index: Int, square: List<SCasePossible<Int>>) in squares.withIndex()) {
            square.forEach { it.setValue(index) };
        }
        println(s.toString())
    }

    @Test
    fun shouldPrintSquare73() {


        val s = SudokuGrid(7, 3, initPossibleValues(7 * 3))
        val squares: List<List<SCasePossible<Int>>> = s.squares
        for ((index: Int, square: List<SCasePossible<Int>>) in squares.withIndex()) {
            square.forEach { it.setValue(index) };
        }
        println(s.toString())
    }

}
