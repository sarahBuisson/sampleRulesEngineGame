import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl
import org.jeasy.rules.core.BasicRule
import org.jeasy.rules.core.DefaultRulesEngine


class MoveFacts(val partie: Partie, var horse: Horse, var position: Position)


class MoveKillRule : BasicRule<MoveFacts>() {

    override fun evaluate(facts: MoveFacts): Boolean {
        return facts.position.content != null
    }

    override fun execute(facts: MoveFacts) {

        facts.position.content!!.events += "kill by " + facts.horse.player.name + " in " + facts.position.name
        facts.position.content!!.position = null//kill

        facts.horse.position?.content = null
        facts.position.content = facts.horse
        facts.horse.position = facts.position

    }

}

class MoveSimpleRule : BasicRule<MoveFacts>() {

    override fun evaluate(facts: MoveFacts): Boolean {
        return facts.position.content == null
    }

    override fun execute(facts: MoveFacts) {

        facts.horse.position?.content = null
        facts.position.content = facts.horse
        facts.horse.position = facts.position
    }
}


val moveRules = RulesImpl(setOf(MoveKillRule(), MoveSimpleRule()))

fun execTurn(partie: Partie, horse: Horse, position: Position) {
    val fact = MoveFacts(partie, horse, position)
    DefaultRulesEngine<MoveFacts>().fire(moveRules, fact)

}



