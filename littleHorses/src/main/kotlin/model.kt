class Partie {
    val players: List<Player>
    val board: Board

    constructor(size: Int) {
        players = mutableListOf()

        board = Board(size);
        for (i in 1..size) {
            val startPosition = board.road.random();
            players.add(Player(startPosition = startPosition))

        }


    }

    constructor(
            players: List<Player>,
            board: Board
    ) {
        this.players = players
        this.board = board

    }

}

class Player {
    val name: String
    val startPosition: Position
    val stables: List<Position>
    val horses: List<Horse>

    constructor(
            name: String = "" + (1..10).random(),
            startPosition: Position, size: Int = 6
    ) {
        this.name = name
        this.startPosition = startPosition
        stables = mutableListOf()
        for (i in 1..6) {
            stables.add(Position("st" + i))
        }
        horses = mutableListOf()
        for (i in 1..size) {
            horses.add(Horse(this, null))
        }
    }
}

data class Position(var name: String = "" + (1..59).random(), var content: Horse? = null) {}


class Horse(
        val player: Player,
        var position: Position? = null,
        val events: MutableList<String> = mutableListOf<String>()
)

class Board {
    private var size: Int
    val road: List<Position>


    constructor(size: Int = 8) {
        this.size = size
        road = mutableListOf<Position>()
        for (i in 1..size * 4 * 4) {
            road.add(Position(name = "" + i))
        }

    }

    fun nextPosition(position: Position): Position {
        return road[(road.indexOf(position) + 1) % road.size]
    }

    fun previousPosition(position: Position): Position {
        return road[(road.size + road.indexOf(position) - 1) % road.size]
    }


}


fun printBoard(partie: Partie) {

    partie.board.road.forEach { position ->
        if (position.content == null) if (partie.players.any { it.startPosition == position }) print(
                "s "
        ) else print(". ") else print(position.content!!.player.name + " ")
    }
    println("")
    partie.players.forEach {
        it.stables.forEach { if (it.content == null) print(". ") else print(it.content!!.player.name) }
        println("")
    }


}
