package fr.perso.sudoku;

import fr.perso.Grid
import fr.perso.SCasePossible
import kotlin.math.truncate


class SudokuGrid<Type> : Grid<Type, SCasePossible<Type>, Iterable<SCasePossible<Type>>> {


    lateinit var squares: List<List<SCasePossible<Type>>>

    constructor(
            width: Int,
            height: Int = width,
            possibles: List<Type>
    ) : super(width, height, possibles) {

        val size = height * width - 1


        // init squares

        val msquares = mutableListOf<MutableList<SCasePossible<Type>>>()

        for (s in 0..size) {
            msquares.add(mutableListOf<SCasePossible<Type>>())

            val sx = (s % width) * height
            val sy = truncate(s.toDouble() / width).toInt() * width
            //  println("----"+s+" : "+sx+" "+sy+" "+s/(height)+" |"+truncate(s.toDouble()/width).toInt())
            for (x in 0..height - 1) {
                for (y in 0..width - 1) {


                    //   println(""+(x + sx)+" "+( y + sy))
                    msquares[s].add(this.get(x + sx, y + sy))


                }
            }

        }
        squares = msquares
        groups = columns + rows + squares


    }

    override fun toString(): String {
        var str = "($width $height)"

        for (y in 0 until this.possibles.size) {
            if (y % width == 0)
                str += "_ _ _ _ _ _\n"
            for (x in 0 until this.possibles.size) {
                if (x % height == 0)
                    str += "|"
                str += get(x, y).getValue() ?: "."

            }

            str += "\n"
        }


        return str


    }

    override fun toType(str: String): Type {
        return str.toInt() as Type
    }

    override fun clone(): SudokuGrid<Type> {
        val nGrid = SudokuGrid<Type>(width, height, possibles)
        nGrid.fillGrid(this)
        return nGrid;
    }
}
