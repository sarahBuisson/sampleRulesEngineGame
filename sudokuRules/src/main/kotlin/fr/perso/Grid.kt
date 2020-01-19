package fr.perso

open class SCase<TPossible> {

    open val x: Int
    open val y: Int
    private var value: TPossible? = null

    constructor(
            x: Int,
            y: Int,
            value: TPossible? = null
    ) {
        this.x = x;
        this.y = y;
        this.value = value

    }

    override fun toString() = "$x-$y = $value"

    open fun setValue(value: TPossible?) {
        this.value = value
    }

    open fun getValue(): TPossible? = value
}


class SCasePossible<TPossible> :
        SCase<TPossible> {
    private var possibles: MutableList<TPossible>
    var solution: TPossible? = null

    constructor(
            x: Int,
            y: Int,
            value: TPossible? = null,
            possibles: List<TPossible> = mutableListOf<TPossible>()

    ) : super(x, y, value) {
        this.possibles = possibles.toMutableList()
    }

    fun getPossibles(): List<TPossible> = possibles;

    fun removePossibilite(possible: TPossible) {
        this.possibles.remove(possible)
        if (possible == solution) {
            println("ERROR")
        }
    }

    override fun setValue(value: TPossible?) {
        super.setValue(value)
        this.possibles = mutableListOf(value!!)
        if (value != solution && solution != null) {
            println("ERROR")
        }
    }

    fun removePossibilites(possiblesToRemove: Iterable<TPossible>) {
        this.possibles.removeAll(possiblesToRemove)
        if (possiblesToRemove.contains(solution)) {
            println("ERROR")
        }
    }

    override fun toString() = super.toString() + possibles

    fun resetCase(possibles: List<TPossible>) {
        this.possibles.clear()
        this.possibles.addAll(possibles)
        if (super.getValue() != null)
            this.solution = super.getValue()
        super.setValue(null)
    }

    fun fill(otherCase: SCasePossible<TPossible>) {
        if (otherCase.getValue() != null) {
            setValue(otherCase.getValue())
            solution = getValue()
        }

        possibles = otherCase.getPossibles().toMutableList()
    }

}

fun initPossibleValues(size: Int) = IntRange(1, size).toList()


abstract class Grid<Type, TCase : SCasePossible<Type>, TGroupe : Iterable<SCasePossible<Type>>> : Iterable<TCase> {
    override fun iterator(): Iterator<TCase> {
        return columns.flatMap { it.asIterable() }.listIterator()
    }

    val width: Int
    val height: Int
    lateinit var grid: List<List<TCase>>
    lateinit var columns: List<List<TCase>>
    lateinit var rows: List<List<TCase>>
    lateinit var groups: List<TGroupe>
    lateinit var possibles: List<Type>

    constructor(width: Int, height: Int = width, possibles: List<Type>) {
        this.width = width;
        this.height = height;
        this.possibles = possibles;
        var mgrid = mutableListOf<MutableList<TCase>>()


        //init Grid
        val size = possibles.size - 1
        for (y in 0..size) {
            mgrid.add(mutableListOf<TCase>())
            for (x in 0..size) {
                mgrid.get(y).add(SCasePossible(x, y, possibles = possibles) as TCase)
            }
        }

        grid = mgrid.toList()
        //init rows
        rows = mgrid;
        //init columns

        val mcolumns = mutableListOf<MutableList<TCase>>()
        columns = mcolumns;
        for (x in 0..size) {


            mcolumns.add(mutableListOf<TCase>())
            for (y in 0..size) {
                mcolumns.get(x).add(grid.get(y).get(x))
            }


        }
    }


    fun get(x: Int, y: Int) = grid[y][x]


    abstract fun toType(str: String): Type

    override fun toString(): String {
        var str = ""

        for (y in 0 until this.possibles.size) {

            for (x in 0 until this.possibles.size) {

                str += get(x, y).getValue() ?: "."

            }

            str += "\n"
        }


        return str


    }

    fun toStringPossi(): String {
        var str = "($width $height)"

        for (y in 0 until this.possibles.size) {
            for (x in 0 until this.possibles.size) {
                str += "" + get(x, y).getPossibles() + " ; "

            }

            str += "\n"
        }


        return str


    }

    abstract fun clone(): Grid<Type, TCase, TGroupe>

    open fun fillGrid(otherGridC: Any) {
        var otherGrid = otherGridC as Grid<Type, TCase, TGroupe>

        this.forEach {
            val otherCase = otherGrid.get(it.x, it.y)
            it.fill(otherCase)
        }
    }

    open fun fill(
            content: String,
            separators: List<Char> = listOf(' ', '\n', '\r'),
            nullPossi: List<Char> = listOf('x', '0')
    ): CharIterator {
        val contentTrim = content.replace("\n", "").replace("\r", "").replace(" ", "")


        var iter = contentTrim.iterator()
        val size = this.possibles.size - 1
        for (y in 0..size) {
            for (x in 0..size) {
                val next = iter.next();
                if (!nullPossi.contains(next)) {
                    get(x, y).solution = (toType("" + next))
                    get(x, y).setValue(toType("" + next))
                }


            }
        }
        return iter;
    }


    fun nbOfFreePossibilite(): Int {
        return this.sumBy { it.getPossibles().size }
    }

    fun size(): Int {
        return this.columns.size * this.rows.size
    }

    fun clear() {
        forEach {
            it.resetCase(this.possibles)
        }
    }

    fun resetPossibles() {
        this.forEach {
            if (it.getValue() == null) {
                it.resetCase(this.possibles)
            }
        }
    }

}
