package fr.perso.skyscraper

import fr.perso.Grid
import fr.perso.SCasePossible
import fr.perso.initPossibleValues


class SkyScraperLine : Collection<SCasePossible<Int>> {

    var view: Int = 0

    var content = ArrayList<SCasePossible<Int>>()


    constructor(data: Collection<SCasePossible<Int>>) {
        content.addAll(data)
    }

    override fun contains(element: SCasePossible<Int>): Boolean = content.contains(element)
    override fun containsAll(elements: Collection<SCasePossible<Int>>): Boolean = content.containsAll(elements)
    override fun isEmpty(): Boolean = content.isEmpty()

    override val size: Int
        get() = content.size

    operator fun get(index: Int): SCasePossible<Int> = content.get(index)

    override fun iterator(): Iterator<SCasePossible<Int>> = content.iterator()

    fun subList(fromIndex: Int, toIndex: Int): List<SCasePossible<Int>> = content.subList(fromIndex, toIndex)

    override fun toString(): String {
        return "$view:" + content.toString()
    }

}

class SkyScraperGrid : Grid<Int, SCasePossible<Int>, SkyScraperLine> {


    lateinit var column1: List<SkyScraperLine>
    lateinit var column2: List<SkyScraperLine>
    lateinit var row1: List<SkyScraperLine>
    lateinit var row2: List<SkyScraperLine>

    constructor(
            width: Int,
            possibles: List<Int>
    ) : super(width, width, initPossibleValues(width)) {


        var mcolumn1 = mutableListOf<SkyScraperLine>()
        var mcolumn2 = mutableListOf<SkyScraperLine>()
        var mrow1 = mutableListOf<SkyScraperLine>()
        var mrow2 = mutableListOf<SkyScraperLine>()
        for (i in 0..width - 1) {
            mcolumn1.add(SkyScraperLine(columns.get(i)))
            mcolumn2.add(SkyScraperLine(columns.get(i).reversed()))
            mrow1.add(SkyScraperLine(rows.get(i)))
            mrow2.add(SkyScraperLine(rows.get(i).reversed()))

        }

        column1 = mcolumn1.toList()
        column2 = mcolumn2.toList()
        row1 = mrow1.toList()
        row2 = mrow2.toList()



        groups = column1 + column2 + row1 + row2
    }

    override fun toType(str: String): Int {
        return str.toInt() as Int
    }

    override fun clone(): SkyScraperGrid {
        val nGrid = SkyScraperGrid(width, possibles)
        nGrid.fillGrid(this)
        return nGrid;
    }


    override fun fill(other: String, separators: List<Char>, nullPossi: List<Char>): CharIterator {
        var iter = super.fill(other, separators, nullPossi)


        for (x in 0..this.possibles.size - 1) {
            val next = iter.next();
            if (!nullPossi.contains(next)) {
                column1[x].view = ("" + next).toInt()
            }
        }
        for (x in 0..this.possibles.size - 1) {
            val next = iter.next();
            if (!nullPossi.contains(next))
                column2[x].view = ("" + next).toInt()
        }
        for (x in 0..this.possibles.size - 1) {
            val next = iter.next();
            if (!nullPossi.contains(next))
                row1[x].view = ("" + next).toInt()
        }
        for (x in 0..this.possibles.size - 1) {
            val next = iter.next();
            if (!nullPossi.contains(next))
                row2[x].view = ("" + next).toInt()
        }
        return iter;
    }

    fun fillGrid(otherGrid: SkyScraperGrid) {
        super.fillGrid(otherGrid)
        this.groups.forEachIndexed { index, group ->
            group.view = otherGrid.groups[index].view

        }

    }


    override fun toString(): String {
        var str = "($width $height)\n    "


        for (x in 0 until this.possibles.size) {
            str += column1.get(x).view
            str += ""
        }

        str += "\n   -------\n"
        for (y in 0 until this.possibles.size) {
            str += row1[y].view
            str += " | "

            for (x in 0 until this.possibles.size) {

                str += get(x, y).getValue() ?: "."

            }
            str += " | "
            str += row2[y].view
            str += "\n"
        }
        str += "   --------\n    "
        for (x in 0 until this.possibles.size) {
            str += column2.get(x).view
            str += ""
        }

        return str


    }

    fun toStringExport(): String {
        var str = ""
        str += super.toString()

        for (x in 0..this.possibles.size - 1) {
            str += column1[x].view
        }
        str += "\n"
        for (x in 0..this.possibles.size - 1) {
            str += column2[x].view
        }
        str += "\n"
        for (x in 0..this.possibles.size - 1) {
            str += row1[x].view
        }
        str += "\n"
        for (x in 0..this.possibles.size - 1) {
            str += row2[x].view
        }
        str += "\n"

        return str


    }
}
