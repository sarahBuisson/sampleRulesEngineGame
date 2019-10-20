import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.BasicRule
import org.jeasy.rules.core.DefaultRulesEngine


class WinFacts(val partie: Partie,  var winner:Player?=null)


class WinneRule : BasicRule<WinFacts>() {

    override fun evaluate(facts: WinFacts): Boolean {
        return facts.partie.players.any { player-> playerWin(player) }
    }

    override fun execute(facts: WinFacts) {
        facts.winner=facts.partie.players.find { player-> playerWin(player) }
    }

    private fun playerWin(player: Player) = player.stables.all { it.content != null }||player.horses.all { player.stables.contains(it.position) }

}


val winRules = Rules(setOf(WinneRule()))

fun someoneIsWinning(partie: Partie): Player? {
    val fact = WinFacts( partie)
    DefaultRulesEngine<WinFacts>().fire(winRules, fact);
    return fact.winner;
}



