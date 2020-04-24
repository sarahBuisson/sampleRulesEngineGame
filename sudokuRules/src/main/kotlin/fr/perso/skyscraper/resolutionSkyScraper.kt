package fr.perso.skyscraper

import fr.perso.SCasePossible
import fr.perso.rules.*
import org.jeasy.rules.api.Rule
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl
import org.jeasy.rules.core.BasicRule
import kotlin.math.max


/**
 * count the number of viewed case
 */
fun countViewableCase(iter: Iterable<Int?>): Int {

    var max = 0;
    var count = 0;
    for (v in iter) {
        if (v!! > max) {
            max = v;
            count++;
        }
    }
    return count;
}

/*
remove the bigger possible at the beginnig in order to allow the view count.
 */
fun removePossiForAllowTheView(list: Iterable<SCasePossible<Int>>, count: Int, minPossi: Int, maxPossi: Int) {


    var maxPossi: Int = list.last().getPossibles().maxBy { it.hashCode() }!! + 1
    list.reversed().forEachIndexed { index, case: SCasePossible<Int> ->
        case.getPossibles().filter { pos -> pos >= maxPossi }
                .forEach { pos -> case.removePossibilite(pos) }
        maxPossi = case.getPossibles().maxBy { it.hashCode() }!!


    }


}


/**
 * if all the tower in the list should be vieawable because of the small size of the list,
 * then it will Remove the min possible and max possible in order to allow each tower to be visible
 */
fun removePossiForACrescentView(list: Collection<SCasePossible<Int>>, count: Int) {

    if (list.size == count) {
        var maxPossi: Int = list.last().getPossibles().maxBy { it.hashCode() }!! + 1
        var minPossi: Int = list.first().getPossibles().minBy { it.hashCode() }!! - 1

        list.forEachIndexed { index, case: SCasePossible<Int> ->


            case.getPossibles().filter { pos -> pos <= minPossi }
                    .forEach { pos -> case.removePossibilite(pos) }

            minPossi = case.getPossibles().minBy { it.hashCode() }!!
        }

    }


}


fun minPossi(case: SCasePossible<Int>): Int {
    return case.getPossibles().first()
}

fun maxPossi(case: SCasePossible<Int>): Int {
    return case.getPossibles().last()
}

/*
an array of boolean about if the case (with the same index) of the line is currently be visible (true), hidden(false) or unknow(null)

 the difference with whichTowerShouldBeVisible method is it ignore the view count of the line.
 Consequently if a case should be visible in order to valid the view count, but have the possibility to stay hidden,
 whichTowerShouldBeVisible will put TRUE, when  lineVisibility will put NULL
 */
fun lineVisibility(line: SkyScraperLine): List<Boolean?> {
    val lineVisibility = ArrayList<Boolean?>(line.size)

    var min = 0;
    var max = 0;
    var count = 0;

    line.forEachIndexed { index, sCasePossible ->
        if (minPossi(sCasePossible) > max) {

            lineVisibility.add(true);
            min = minPossi(sCasePossible)
            max = maxPossi(sCasePossible)
            count++
        } else
            if (maxPossi(sCasePossible) <= min) {
                lineVisibility.add(false)
            } else {
                lineVisibility.add(null)
            }


    }


    return lineVisibility
}

/*
an array of boolean about if the case (with the same index) of the line should be visible (true), hidden(false) or unknow(null)

 the diffirence with lineVisibility method is this time,
  we transmit the status a case should have in order to allow the view-count of the line to be valid
   Consequently if a case should be visible in order to valid the view count, but have the possibility to stay hidden,
 whichTowerShouldBeVisible will put TRUE, when  lineVisibility will put NULL

 */
fun whichTowerShouldBeVisible(line: SkyScraperLine): List<Boolean?> {
    val actualLineVisibility = lineVisibility(line).toMutableList();
    var minIndexBiggerTower = max(line.view, line.indexOfFirst { it.getPossibles().contains(line.size) })
    var maxIndexBiggerTower = max(line.view, line.indexOfLast { it.getPossibles().contains(line.size) })
    val shouldBeVisible: List<Boolean?>

    val allTheCurrentlyVisibleAllowTheViewCount =
            actualLineVisibility.subList(0, maxIndexBiggerTower + 1).filter { it == true }.size == line.view
    if (allTheCurrentlyVisibleAllowTheViewCount) {
        //only the one we are sure are visible should stay visible
        //TODO : don't apply to the one after the mac val !
        shouldBeVisible = actualLineVisibility.map {
            it ?: false
        }


    } else {
        val allTheCurentlyVisibleAndNotSureIfVisibleShouldBeVisibleToAllowTheCount =
                actualLineVisibility.subList(0, maxIndexBiggerTower + 1).filter { it != false }.size == line.view
        if (allTheCurentlyVisibleAndNotSureIfVisibleShouldBeVisibleToAllowTheCount) {
            //all who can be visible should be show
            //TODO : don't apply to the one after the mac val !
            shouldBeVisible = actualLineVisibility.mapIndexed { index, it -> it ?: true }

        } else {

            shouldBeVisible = actualLineVisibility

        }
    }
    return shouldBeVisible;
}

fun removPossiAccordingToWhichTowerShouldBeVisible(line: SkyScraperLine) {
    val shouldBeVisible = whichTowerShouldBeVisible(line);

    var minIndexBiggerTower = max(line.view, line.indexOfFirst { it.getPossibles().contains(line.size) })
    var maxIndexBiggerTower = max(line.view, line.indexOfLast { it.getPossibles().contains(line.size) })
    var min = 0;
    var count = 0;
    line.subList(0, minIndexBiggerTower).forEachIndexed { index, sCasePossible ->


        if (shouldBeVisible[index] == true) {
            sCasePossible.removePossibilites(IntRange(0, min))

            //remove every possi who can hide before the building
            line.subList(0, index).forEach { it.removePossibilite(maxPossi(sCasePossible)) }
            //remove every possi who cannot hide for the buildig who should hide after the building
            if (index + 1 < line.size && shouldBeVisible[index + 1] == false)
                line[index + 1].removePossibilites(IntRange(maxPossi(sCasePossible), line.size))


        } else if (shouldBeVisible[index] == false) {
            val lastVisible = line.subList(0, index)
                    .filterIndexed { index, it -> shouldBeVisible[index] != false }.last()
            lastVisible.removePossibilites(IntRange(0, minPossi(sCasePossible)))
            sCasePossible.removePossibilites(IntRange(maxPossi(lastVisible), line.size))
        }

        /*
    if (minPossi(sCasePossible) > min) {

        if (whichTowerShouldBeVisible.get(index) == true) {
            sCasePossible.removePossibilites(IntRange(0, min))
        }
        if (minPossi(sCasePossible) > min) {
            min = minPossi(sCasePossible)
            count++
        }
    }*/

    }


    line.forEachIndexed { index, sCasePossible ->


    }


}


class ComplexeVisiblePossibleRule : BasicRule<SkyScraperLine>() {

    override fun evaluate(facts: SkyScraperLine): Boolean = true
    override fun execute(group: SkyScraperLine) {
        removPossiAccordingToWhichTowerShouldBeVisible(group)
    }
}


class StairViewRule : BasicRule<SkyScraperLine>() {

    override fun evaluate(facts: SkyScraperLine): Boolean {
        return facts.view == facts.size
    }

    override fun execute(group: SkyScraperLine) {
        group.forEachIndexed { index, sCasePossible -> sCasePossible.setValue(index + 1) }
    }

}


class OneViewVisibleRule : BasicRule<SkyScraperLine>() {

    override fun evaluate(facts: SkyScraperLine): Boolean {
        return facts.view == 1
    }

    override fun execute(group: SkyScraperLine) {
        group.first().setValue(group.size)
    }

}


class RemovePossibleForViewGroup : BasicRule<SkyScraperLine>() {

    override fun evaluate(facts: SkyScraperLine): Boolean = true
    override fun execute(group: SkyScraperLine) {

        group.mapIndexed { index, it ->
            if (index + 1 < group.view) {
                for (i in group.size - group.view + index + 1 + 1..group.size) {
                    it.removePossibilite(i)
                }


            }

        }

    }

}


fun checkIfViewCountRespected(ensemble: SkyScraperLine, values: List<Int>): List<String> {

    if (ensemble.none { it.getValue() == null }) {


        val countedTower = countViewableCase(ensemble.map { it.getValue() })
        if (ensemble.view != countedTower) {
            return listOf("ensemble view $countedTower!=${ensemble.view}")
        }
    }
    return emptyList();

}


class RemoveMax() : BasicRule<SkyScraperLine>() {

    override fun execute(group: SkyScraperLine) {
        removePossiForAllowTheView(group, group.view, 0, group.size)

    }

}

class RemoveMin(
) : BasicRule<SkyScraperLine>() {

    override fun execute(group: SkyScraperLine) {
        removePossiForACrescentView(group, group.view)
    }

}


class RemoveUnrespectingCombination(
) : BasicRule<SkyScraperLine>() {

    override fun execute(group: SkyScraperLine) {
        val combinations = combinationRespectingUnicity(group)//This combination respects JUST the unicity rule, but not always the view count

        val possiRespectingView = mutableListOf<MutableList<Int>>()
        combinations.filter {
            val count = countViewableCase(it)
            (count == group.view)

        }.forEach {

            for (i in 0..group.size) {
                possiRespectingView.add(mutableListOf())
            }

            it.forEachIndexed { index, possi ->
                possiRespectingView[index].add(possi)
            }

        }

        group.forEachIndexed { index, case ->
            case.getPossibles().forEach { possi ->
                if (!possiRespectingView[index].contains(possi))
                    case.removePossibilite(possi)
            }

        }


    }

}


class ResolveSkyScraperGrid() :
        ResolveGrid<Int, SkyScraperLine, SkyScraperGrid>() {
    override var gridRules: Rules<out SkyScraperGrid> = RulesImpl(
            setOf(
                    //  UseRandomHypothesis(this::isGridPossibleValid, this::execute)
            )
    )
    override var caseRules: Rules<SCasePossible<Int>> = RulesImpl(setOf(OnePossibiliteSet<Int>()))
    override var groupeRules: Rules<out SkyScraperLine> = RulesImpl(
            setOf<Rule<SkyScraperLine>>(
                    OneViewVisibleRule(),
                    StairViewRule(),
                    RemoveSureValueFromTheRestOfTheGroup<Int, SkyScraperLine>(),
                    RemovePossibleLockFromTheGroup<Int, SkyScraperLine>(),
                    RemovePossibleForViewGroup(),
                    ComplexeVisiblePossibleRule(),
                    RemoveMin(),
                    RemoveMax(),
                    RemoveUnrespectingCombination()
            )
    )

    override fun errorInTheGrid(grid: SkyScraperGrid): List<Any> {
        return grid.groups.map { group ->

            val respectUnicity = respectUnicity(group, grid.possibles)
            val respectPossiblePresence = respectPossiblePresence(group, grid.possibles)
            val checkIfViewCountRespected = checkIfViewCountRespected(group, grid.possibles)
            val errors = mutableListOf<Any>()
            errors.addAll(respectUnicity)
            errors.addAll(respectPossiblePresence)
            errors.addAll(checkIfViewCountRespected)
            errors

        }.flatten()
    }

    override fun isGridPossibleValid(grid: SkyScraperGrid): Boolean {

        val errors = errorInTheGrid(grid)


        return errors.isEmpty()

    }
}
