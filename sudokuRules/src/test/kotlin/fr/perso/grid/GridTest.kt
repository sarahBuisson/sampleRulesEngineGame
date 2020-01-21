import fr.perso.SCasePossible
import fr.perso.rules.RemovePossibleLockFromTheGroup
import fr.perso.rules.combinations
import org.junit.Test
import kotlin.test.assertEquals

class GridTest {


    @Test
    fun shouldListAllAsso() {


        var assos2_of_3 = combinations((1..3).toList(), 2)
        println(assos2_of_3)
        assertEquals(listOf(setOf(2, 1), setOf(3, 1), setOf(3, 2)), assos2_of_3)

        var assos2_of_5 = combinations((1..5).toList(), 2)
        println(assos2_of_5)

        var assos4_of_5 = combinations((1..5).toList(), 4)
        println(assos4_of_5)
        assertEquals(listOf(setOf(1, 2, 3, 4), setOf(1, 2, 3, 5), setOf(1, 2, 4, 5), setOf(1, 3, 4, 5), setOf(2, 3, 4, 5)), assos4_of_5)
    }


    @Test
    fun shouldRemoveLockSize2() {
        println("shouldRemoveLock")
        val c1 = SCasePossible(0, 1, possibles = listOf(1, 2))
        val c2 = SCasePossible(0, 1, possibles = listOf(1, 2))

        val c3 = SCasePossible(0, 1, possibles = listOf(1, 2, 3))
        val group = mutableListOf(c1, c2, c3)

        RemovePossibleLockFromTheGroup<Int, Iterable<SCasePossible<Int>>>(2).execute(group)

        println(group)
        assertEquals(setOf(1, 2), c1.getPossibles().toSet())
        assertEquals(setOf(1, 2), c2.getPossibles().toSet())
        assertEquals(setOf(3), c3.getPossibles().toSet())

    }

    @Test
    fun shouldRemoveLockSize3() {
        println("shouldRemoveLock")
        val c1 = SCasePossible(0, 1, possibles = listOf(1, 2, 3))
        val c2 = SCasePossible(0, 1, possibles = listOf(1, 2, 3))

        val c3 = SCasePossible(0, 1, possibles = listOf(1, 2, 3))
        val c4 = SCasePossible(0, 1, possibles = listOf(1, 2, 3, 4))
        val c5 = SCasePossible(0, 1, possibles = listOf(1, 2, 3, 4, 5))
        val group = mutableListOf(c1, c2, c3, c4, c5)

        RemovePossibleLockFromTheGroup<Int, Iterable<SCasePossible<Int>>>().execute(group)

        println(group)
        assertEquals(3, c1.getPossibles().size)
        assertEquals(3, c2.getPossibles().size)
        assertEquals(3, c3.getPossibles().size)
        assertEquals(1, c4.getPossibles().size)
        assertEquals(2, c5.getPossibles().size)
        assertEquals(4, c4.getPossibles().first())
        assertEquals(4, c5.getPossibles().first())
        assertEquals(5, c5.getPossibles()[1])

    }

    @Test
    fun shouldRemoveLockSize23() {
        println("shouldRemoveLock")
        val c1 = SCasePossible(0, 1, possibles = listOf(1, 2, 3))
        val c2 = SCasePossible(0, 1, possibles = listOf(1, 2))

        val c3 = SCasePossible(0, 1, possibles = listOf(1, 2, 3))
        val c4 = SCasePossible(0, 1, possibles = listOf(1, 2, 3, 4))
        val c5 = SCasePossible(0, 1, possibles = listOf(1, 2, 3, 4, 5))
        val group = mutableListOf(c1, c2, c3, c4, c5)

        RemovePossibleLockFromTheGroup<Int, Iterable<SCasePossible<Int>>>().execute(group)

        println(group)
        assertEquals(3, c1.getPossibles().size)
        assertEquals(2, c2.getPossibles().size)
        assertEquals(3, c3.getPossibles().size)
        assertEquals(1, c4.getPossibles().size)
        assertEquals(2, c5.getPossibles().size)
        assertEquals(4, c4.getPossibles().first())
        assertEquals(4, c5.getPossibles().first())
        assertEquals(5, c5.getPossibles()[1])

    }

}
