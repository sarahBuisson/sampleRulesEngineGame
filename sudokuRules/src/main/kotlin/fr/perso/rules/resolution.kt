package fr.perso.rules

import fr.perso.Grid
import fr.perso.SCasePossible
import org.jeasy.rules.api.RulesEngine
import org.jeasy.rules.api.Rule
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl
import org.jeasy.rules.core.BasicRule
import org.jeasy.rules.core.DefaultRulesEngine
import kotlin.math.min


fun onePossibilite(case: SCasePossible<Any>): Boolean = case.getPossibles().size == 1


fun onePossibiliteSet(case: SCasePossible<Any>) {
    case.setValue(case.getPossibles().first())
}

/**
 * return all the possible subAssociation of an array. ex:
 * [1,2,3]->[[1,2],[1,3],[2,3]]
 */
public fun <Some> combinations(
        thingsToAssociate: List<Some>,
        limitSizeOfAssociation: Int = thingsToAssociate.size - 1
): List<Set<Some>> {

    if (limitSizeOfAssociation == 1) {
        return thingsToAssociate.map { setOf(it) }
    } else {

        var previousAssos = combinations(thingsToAssociate, limitSizeOfAssociation - 1)
        var newAssos = mutableListOf<Set<Some>>()
        for (thing in thingsToAssociate) {
            for (previous in previousAssos) {
                if (!previous.contains(thing))
                    newAssos.add(previous + thing)

            }

        };
        return newAssos.distinct();
    }


}


class OnePossibiliteSet<T>(
) : BasicRule<SCasePossible<T>>() {

    override fun evaluate(case: SCasePossible<T>): Boolean = case.getValue() == null && case.getPossibles().size == 1

    override fun execute(case: SCasePossible<T>) {
        case.setValue(case.getPossibles().first())

    }

}


class RemoveSureValueFromTheRestOfTheGroup<T, GroupT : Iterable<SCasePossible<T>>> : BasicRule<GroupT>() {

    override fun evaluate(facts: GroupT): Boolean = true

    override fun execute(group: GroupT) {

        val values = group.map { it.getValue() }.filterNot { it == null }

        //val values:List<T> = group.mapIndexedNotNull { it .getValue() as T? } as List<T>
        group.filter { it.getValue() == null }.forEach { c -> values.forEach { v -> c.removePossibilite(v!!) } }


    }

}

/**
 * maxSizeLock max size of the lockCombination between case studied
 * lockByCaseFamine: remove possibles of some cases when only this case can take the value of the lockCombination
 * ex: [[3,4][3,4],[1,2,3],[1,2]] ->[[3,4][3,4],[1,2],[1,2]]
 *
 *
 * lockByPossibleFamine remove possibles of otherCase when the value of the lockCombination are the only one who can be taken by some case
 *  * ex: [[1,2,3,4],[1,2],[1,2]] ->[[3,4],[1,2],[1,2]]
 */
class RemovePossibleLockFromTheGroup<T, GroupT : Iterable<SCasePossible<T>>>(var maxSizeLock: Int = 0, val lockByCaseFamine: Boolean = true, val lockByPossibleFamine: Boolean = true) : BasicRule<GroupT>() {

    override fun evaluate(facts: GroupT): Boolean = facts.count { it.getValue() == null } > 2

    override fun execute(group: GroupT) {
        if (maxSizeLock == 0)
            maxSizeLock = group.count() - 2

        val unresolvedsCases = group.filter { it.getValue() == null }
                .sortedBy { it.getPossibles().size }

        val unresolvedValues = group.filter { it.getValue() == null }.flatMap { it.getPossibles() }.distinct()


        for (combSize in 2..maxSizeLock) {
            val combinations = combinations(unresolvedValues, combSize);

            for (combination in combinations) {

                if (lockByPossibleFamine) {
                    //Case 1 : if the combinaison if the only possible for a few-cases, then remove-it from the possible of other cases
                    val casesWithOnlyPossibleOnCombination =
                            unresolvedsCases.filter { combination.containsAll(it.getPossibles()) };
                    if (casesWithOnlyPossibleOnCombination.size == combSize && unresolvedsCases.size > combSize) {
                        val otherCases = unresolvedsCases.minus(casesWithOnlyPossibleOnCombination)
                        otherCases.forEach { it.removePossibilites(combination) }

                    }
                }
                if (lockByCaseFamine) { //TODO : don't work, provoque invalide grid
                    //case 2 If only a few case can take the possible value of this combination, then remove the possible Not In Combination

                    val casesWhoCanTakeCombination =
                            unresolvedsCases.filter { it.getPossibles().intersect(combination).size > 0 };
                    if (casesWhoCanTakeCombination.size == combSize && unresolvedsCases.size > combSize) {
                        casesWhoCanTakeCombination.filter {
                            it.getPossibles().intersect(combination).size != min(
                                    it.getPossibles().size,
                                    combSize
                            )
                        }.forEach {
                            it.removePossibilites(it.getPossibles().filter { p ->
                                !combination.contains(
                                        p
                                )
                            })
                        }
                    }
                }


            }
        }
    }
}

class RemovePossibleLockFromTheGroupOld<T, GroupT : Iterable<SCasePossible<T>>> : BasicRule<GroupT>() {

    override fun evaluate(facts: GroupT): Boolean = true

    override fun execute(group: GroupT) {

        val unresolveds = group.filter { it.getValue() == null }.filter { it.getPossibles().size > 1 }
                .sortedBy { it.getPossibles().size }

        unresolveds.forEach { oneCase ->
            val caseWithSamePossibles = unresolveds
                    .filter { otherCase ->
                        //one case contain all the possible of another.
                        otherCase.getPossibles().intersect(oneCase.getPossibles()).size == min(
                                oneCase.getPossibles().size,
                                otherCase.getPossibles().size
                        ) && otherCase.getPossibles().size <= oneCase.getPossibles().size
                    }
            if (caseWithSamePossibles.size > 1 && caseWithSamePossibles.size == oneCase.getPossibles().size) {//We are in a lock case: only this samePossibles should have this possibles , the other should'nt

                unresolveds.filterNot { ot -> caseWithSamePossibles.contains(ot) }.forEach { otherCase ->

                    oneCase.getPossibles().forEach { possToRemove ->
                        otherCase.removePossibilite(possToRemove)
                    }
                }
            }
        }
    }
}

class FillRandomlyOneCase<Type, GroupType : Iterable<SCasePossible<Type>>, GridType : Grid<Type, SCasePossible<Type>, GroupType>>(
        val isGridPossibleValid: ((GridType) -> Boolean),
        val resolverExecute: ((GridType) -> Unit)


) : BasicRule<GridType>() {

    override fun evaluate(grid: GridType): Boolean = true

    override fun execute(grid: GridType) {
        val nextCase = grid.find { it.getValue() == null }!!
        if (nextCase.getPossibles().size > 1) {
            val newVal = nextCase.getPossibles().random()
            nextCase.setValue(newVal)
        }
    }
}

class UseRandomValueAsSure<Type, GroupType : Iterable<SCasePossible<Type>>, GridType : Grid<Type, SCasePossible<Type>, GroupType>>


    : BasicRule<GridType>() {
    override fun evaluate(grid: GridType): Boolean {


        val gridStillHaveEmptySlot = grid.nbOfFreePossibilite() > grid.size()

        if (gridStillHaveEmptySlot) {
            return true

        }
        return false

    }

    override fun execute(grid: GridType) {

        //  println("resolveWithHypothesis")
        // println(grid)
        val firstCaseToResolve =
                grid.first { it.getValue() == null }

        firstCaseToResolve.setValue(firstCaseToResolve.getPossibles().random())

    }

}


class UseRandomHypothesis<Type, GroupType : Iterable<SCasePossible<Type>>, GridType : Grid<Type, SCasePossible<Type>, GroupType>>(
        val isGridPossibleValid: ((GridType) -> Boolean),
        val resolverExecut: ((GridType) -> Unit)


) : BasicRule<GridType>() {

    override fun evaluate(grid: GridType): Boolean {

        val gridPossibleValid = this.isGridPossibleValid(grid)
        val gridStillHaveEmptySlot = grid.nbOfFreePossibilite() > grid.size()

        if (gridPossibleValid && gridStillHaveEmptySlot) {
            return true

        }
        return false

    }

    override fun execute(grid: GridType) {

        //  println("resolveWithHypothesis")
        // println(grid)
        val sortedCaseWithLowPossible = grid.filter { it.getValue() == null }.filter { it.getPossibles().size > 1 }.sortedBy { it.getPossibles().size }

        var oldNbPossible = grid.sumBy { it.getPossibles().size }
        for (caseWithLowPossible in sortedCaseWithLowPossible) {


            for (hypothesis in caseWithLowPossible.getPossibles().toList()) {
                val x = caseWithLowPossible.x
                val y = caseWithLowPossible.y
                val newGrid: GridType = tryHypothesis(grid, x, y, hypothesis)
                var newNbPossible = grid.sumBy { it.getPossibles().size }
                if (!isGridPossibleValid(newGrid)) {
                    // println("hypothesis fail  $x $y $hypothesis")
                    caseWithLowPossible.removePossibilite(hypothesis)
                    continue
                } else if (newNbPossible == oldNbPossible) {
                    println("this hypothesis don't bring any change  $x $y $hypothesis")
                    //          caseWithLowPossible.removePossibilite(hypothesis)//TODO : should we remove?

                } else {

                    if (newNbPossible == grid.size()) {
                        grid.fillGrid(newGrid)

                    }
                }
            }
        }
    }


    private fun tryHypothesis(
            grid: GridType,
            x: Int, y: Int,
            hypothesis: Type
    ): GridType {

        val newGrid: GridType = grid.clone() as GridType
        newGrid.get(x, y).setValue(hypothesis)

        this.resolverExecut(newGrid)
        return newGrid
    }


}


fun <T> runBook(fact: T, rules: Rules<T>) {
    DefaultRulesEngine<T>().fire(rules, fact)
}


abstract class ResolveGrid<Type,
        GroupType : Iterable<SCasePossible<Type>>,
        GridType : Grid<Type, SCasePossible<Type>, GroupType>>() :
        BasicRule<GridType>() {

    abstract var caseRules: Rules<SCasePossible<Type>>
    abstract var groupeRules: Rules<out GroupType>
    abstract var gridRules: Rules<out GridType>
    override fun evaluate(grid: GridType): Boolean =
            isNotResolved(grid)

    open protected fun isNotResolved(grid: GridType) = grid.any { it.getValue() == null }

    override fun execute(grid: GridType) {

        try {
            var nbPossible = grid.possibles.size * grid.possibles.size * grid.possibles.size + 1
            var newNbPossible = nbPossible - 1
            do {
                grid.forEach {
                    runBook(it, caseRules)
                }

                grid.groups.forEach { group ->
                    runBook(
                            group,
                            groupeRules as Rules<GroupType>
                    )
                    if (!isGridPossibleValid(grid))
                        println("ERROR, grid No More valid")
                }
                nbPossible = newNbPossible
                newNbPossible = grid.sumBy { it.getPossibles().size }


                val gridPossibleValid = isGridPossibleValid(grid)
                val traditionalResolutionDontBringAnything = nbPossible == newNbPossible
                val gridStillHaveEmptySlot = grid.nbOfFreePossibilite() > grid.size()

                if (gridPossibleValid && traditionalResolutionDontBringAnything && gridStillHaveEmptySlot) {
                    runBook(
                            grid,
                            gridRules as Rules<GridType>
                    )
                    newNbPossible = grid.sumBy { it.getPossibles().size }
                }
            } while (newNbPossible < nbPossible)
            //println("no more Change " + newNbPossible + " " + nbPossible)
        } catch (e: Exception) {
            println(e)
            println(e.cause)
        }
    }

    abstract fun isGridPossibleValid(grid: GridType): Boolean;


    fun run(grid: GridType) {
        runBook(grid, RulesImpl(setOf(this)))
    }

    fun addCaseRule(rule: Rule<SCasePossible<Type>>) {
        this.caseRules = RulesImpl(this.caseRules.toSet() + rule)
    }

    fun addGroupeRule(rule: Rule<out GroupType>) {
        val rules = this.groupeRules.toSet() + rule
        this.groupeRules = RulesImpl(rules as Set<Rule<Nothing>>)
    }

    fun addGridRule(rule: Rule<out GridType>) {
        val rules = this.gridRules.toSet() + rule
        this.gridRules = RulesImpl(rules as Set<Rule<Nothing>>)
    }

    fun addAllRules(otherResolver: ResolveGrid<Type, GroupType, GridType>) {
        for (rule in otherResolver.caseRules.toSet()) {
            addCaseRule(rule)
        }
        for (rule in otherResolver.groupeRules.toSet()) {
            addGroupeRule(rule)
        }
        for (rule in otherResolver.gridRules.toSet()) {
            addGridRule(rule)
        }
    }

    abstract fun errorInTheGrid(grid: GridType): List<Any>
}


fun <Type, GroupType : Iterable<SCasePossible<Type>>> combinationRespectingUnicity(group: GroupType): List<List<Type>> {
    if (group.toList().size == 1) {
        return group.map { it.getPossibles() }
    } else {
        var previous = combinationRespectingUnicity(group.toList().subList(1, group.toList().size - 1))
        var nextCombination = mutableListOf<List<Type>>()
        for (prev in previous)
            for (possible in group.first().getPossibles()) {
                if (!prev.contains(possible)) {
                    val combi: List<Type> = listOf<Type>() + possible + prev
                    nextCombination.add(combi)
                }
            }
        return nextCombination

    }
}

