import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.BasicRule
import org.jeasy.rules.core.DefaultRulesEngine

data class MovingFact(
        val partie: Partie,
        val horse: Horse,
        val player: Player,
        var position: Position?,
        val diceValue: Int,
        var distance: Int = diceValue,
        var regularMovingSence: Boolean = true

) {


    var invalid: Boolean = false;
    var comment: String = "";
}


class goToStableRule() : BasicRule<MovingFact>() {

    override fun evaluate(facts: MovingFact): Boolean {
        val stablePosition = facts.partie.board.previousPosition(facts.player.startPosition)
        val horseAtTheEnter = facts.position == stablePosition
        val horseStop = facts.distance == 1
        val firstStableEmpty = facts.player.stables.first().content == null
        val goToStable = horseAtTheEnter && horseStop && facts.diceValue == 1 && firstStableEmpty
        return goToStable
    }

    override fun execute(facts: MovingFact) {
        super.execute(facts)
        facts.position = facts.player.stables.first();
        facts.distance = 0;
        facts.comment += " gostable"
    }

}

class goBAckAfterStableToStableRule() : BasicRule<MovingFact>() {

    override fun evaluate(facts: MovingFact): Boolean {

        val stablePosition = facts.partie.board.previousPosition(facts.player.startPosition)

        return facts.position == stablePosition && facts.diceValue != 1
    }

    override fun execute(facts: MovingFact) {
        super.execute(facts)
        facts.regularMovingSence = false;
        facts.position = facts.partie.board.previousPosition(facts.position!!)
        facts.distance--
        facts.comment += " goBAck"
    }

}

class moveStableRule() : BasicRule<MovingFact>() {

    override fun evaluate(facts: MovingFact): Boolean {
        val currentPosition = facts.player.stables.indexOf(facts.position);
        val stableFree = nextStable(facts).content == null
        return currentPosition != -1 && (currentPosition + 2) == facts.diceValue && stableFree
    }

    override fun execute(facts: MovingFact) {
        super.execute(facts)
        facts.position = nextStable(facts)
        facts.distance = 0;
        facts.comment += " mstable"


    }

    private fun nextStable(facts: MovingFact) = facts.player.stables.get(facts.diceValue - 1)

}


class move() : BasicRule<MovingFact>() {
    override fun evaluate(facts: MovingFact): Boolean {

        val horseInTheRoad = facts.position != null && facts.partie.board.road.contains(facts.position!!)
        val evaluate = horseInTheRoad && facts.regularMovingSence && facts.distance > 0
        return evaluate
    }


    override fun execute(facts: MovingFact) {
        super.execute(facts)
        facts.position = facts.partie.board.nextPosition(facts.position!!)
        facts.distance--
    }

}

class moveBack() : BasicRule<MovingFact>() {
    override fun evaluate(facts: MovingFact): Boolean {
        val horseInTheRoad = facts.position != null && facts.partie.board.road.contains(facts.position!!)

        return horseInTheRoad && !facts.regularMovingSence && facts.distance > 0
    }

    override fun execute(facts: MovingFact) {
        super.execute(facts)
        facts.position = facts.partie.board.previousPosition(facts.position!!)
        facts.distance--
    }

}

class goInTheRoad() : BasicRule<MovingFact>() {
    override fun evaluate(facts: MovingFact): Boolean {
        val startPositionEmpty = facts.player.startPosition.content == null
        val horseOutOdTheRoad = facts.position == null
        return horseOutOdTheRoad && facts.diceValue == 6 && startPositionEmpty
    }


    override fun execute(facts: MovingFact) {
        super.execute(facts)
        facts.position = facts.player.startPosition
        facts.distance = 0
        facts.comment += "enter "
    }

}

class lastcaseOccupedByAHorse() : BasicRule<MovingFact>() {
    override fun evaluate(facts: MovingFact): Boolean {

        val horseOnTheCase = facts.position?.content
        val horseCannotBeTaken = facts.player.horses.contains(horseOnTheCase)//no kill
        val horseWillStopOnCase = facts.distance == 0
        return horseOnTheCase != null && horseCannotBeTaken && horseWillStopOnCase
    }


    override fun execute(facts: MovingFact) {
        super.execute(facts)
        facts.invalid = true
        facts.distance = 0
    }

}

//getAPossibleDestination for a horse. Don't move-it
val rulesPossibleMove =
        Rules<MovingFact>(
                setOf(
                        moveStableRule(),
                        goToStableRule(),
                        goBAckAfterStableToStableRule(),
                        goInTheRoad(),
                        moveBack(),
                        move(),
                        lastcaseOccupedByAHorse()
                )
        )

fun runMovePossibleRules(partie: Partie, player: Player, dice: Int): Map<Horse, MovingFact> {
    val mapRet = HashMap<Horse, MovingFact>()
    player.horses.forEach { horse ->
        var previousFacts: MovingFact
        var nextFacts: MovingFact = MovingFact(partie, horse, player, horse.position, dice)

        do {
            previousFacts = nextFacts
            nextFacts = previousFacts.copy()
            DefaultRulesEngine<MovingFact>().fire(rulesPossibleMove, nextFacts)

        } while (!previousFacts.equals(nextFacts))


        if (!nextFacts.invalid && nextFacts.position != null && nextFacts.position != horse.position) {
            println("next position " + horse.player.name + " : " + horse.position + " ->" + nextFacts.position)
            mapRet.put(horse, nextFacts)
        }
    }
    return mapRet;
}



