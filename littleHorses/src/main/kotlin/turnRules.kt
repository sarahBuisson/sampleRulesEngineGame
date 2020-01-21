import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.BasicRule
import org.jeasy.rules.core.DefaultRulesEngine

class TurnFacts(val dice: Int, var player: Player, val partie: Partie)


class NextPlayerRule : BasicRule<TurnFacts>() {

    override fun evaluate(facts: TurnFacts): Boolean {
        return facts.dice != 6
    }

    override fun execute(facts: TurnFacts) {
        val index = (facts.partie.players.indexOf(facts.player) + 1) % facts.partie.players.size
        facts.player = facts.partie.players[index]
    }

}


val turnRules = Rules(setOf(NextPlayerRule()))

fun whoIsTheTurn(partie: Partie, player: Player, dice: Int): Player {
    val fact = TurnFacts(dice, player, partie)
    DefaultRulesEngine<TurnFacts>().fire(turnRules, fact);
    return fact.player;
}


fun runPartieAuto(partie: Partie) {
    var nextPlayer = partie.players.first()

    var index = 0;
    while (someoneIsWinning(partie) == null) {
        index++
        val dice = (1..6).random()
        println("" + index + " -> " + dice)
        val possibles = runMovePossibleRules(partie, nextPlayer, dice)
        if (possibles.isNotEmpty()) {

            val choosen = possibles.toList().random()
            println(choosen)
            choosen.first.events.add(choosen.second.comment + " " + choosen.second.position!!.name)
            execTurn(partie, choosen.first, choosen.second.position!!)
        }
        nextPlayer = whoIsTheTurn(partie, nextPlayer, dice)


        printBoard(partie)
    }
}
