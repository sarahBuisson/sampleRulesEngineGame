package fr.perso.skyscraper;


import fr.perso.initPossibleValues
import fr.perso.rules.RemoveSureValueFromTheRestOfTheGroup
import fr.perso.rules.runBook
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.RulesImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class SkyscraperGridResolveTest {


    @Test
    fun removePossi() {
        val gridGenerated = SkyScraperGrid(5, initPossibleValues(5));
        val line = gridGenerated.column1.first()

        line.view = 1
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)
        line.view = 2
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)
        line.view = 3
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)

        line.view = 4
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)


        line.view = 5
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)
    }

    @Test
    fun removePossi6() {
        val gridGenerated = SkyScraperGrid(6, initPossibleValues(6));
        val line = gridGenerated.column1.first()

        line.view = 1
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)


        line.view = 2
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)
        line.view = 3
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)

        line.view = 4
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)


        line.view = 5
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)
        line.view = 6
        runBook(line, RulesImpl(setOf(RemovePossibleForViewGroup())))
        println(line)

    }


    @Test
    fun shouldBeVisible() {
        val gridGenerated = SkyScraperGrid(5, initPossibleValues(5));
        val line = gridGenerated.column1.first()

        line.view = 2
        line.get(3).setValue(5)
        runBook(line, RulesImpl(setOf(

                RemoveSureValueFromTheRestOfTheGroup(),
                ComplexeVisiblePossibleRule()
        )))
        println(line)

    }

    @Test
    fun shouldBeVisible2() {
        val gridGenerated = SkyScraperGrid(5, initPossibleValues(5));
        val line = gridGenerated.column1.first()

        line.view = 2
        line.get(1).setValue(5)
        runBook(line, RulesImpl(setOf(

                RemoveSureValueFromTheRestOfTheGroup(),
                ComplexeVisiblePossibleRule())))
        println(line)

    }

    @Test
    fun shouldBeVisible3() {
        val gridGenerated = SkyScraperGrid(5, initPossibleValues(5));
        val line = gridGenerated.column1.first()

        line.view = 3
        line.get(4).setValue(5)
        runBook(line, RulesImpl(setOf(
                RemovePossibleForViewGroup(),
                RemoveSureValueFromTheRestOfTheGroup(),
                ComplexeVisiblePossibleRule())))

        println(line)

    }


    @Test
    fun shouldResolved4() {
        val gridGenerated = SkyScraperGrid(5, initPossibleValues(5));
        val line = gridGenerated.column1.first()

        line.view = 4
        line.get(3).setValue(5)
        line.get(4).setValue(4)
        runBook(line, ResolveSkyScraperGrid().groupeRules as Rules<SkyScraperLine>)
        println(line)

    }

    @Test
    fun shouldResolved3() {
        val gridGenerated = SkyScraperGrid(5, initPossibleValues(5));
        val line = gridGenerated.column1.first()

        line.view = 3
        line.get(3).setValue(5)
        line.get(4).setValue(4)
        runBook(line, ResolveSkyScraperGrid().groupeRules as Rules<SkyScraperLine>)
        println(line)

    }

}
