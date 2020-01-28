import fr.perso.labyrinth.freezone.gameplay.initPartie
import fr.perso.labyrinth.freezone.gameplay.playerInteractWith
import fr.perso.labyrinth.freezone.model.FreeZone
import fr.perso.labyrinth.freezone.generation.LabFiller
import fr.perso.labyrinth.freezone.generation.createCorridor
import fr.perso.labyrinth.freezone.generation.createLab
import org.junit.Test

class FreeZoneTest {


    @Test
    fun shouldGenerateLabyrinth() {

        val lab = createLab(10)

        println(lab)

    }

    @Test
    fun shouldFillLabyrinth() {
        val lab = createLab(10);
        println(lab)
        LabFiller<FreeZone>().fillLab(lab, lab.first(), 10, 1);
        println(lab)
    }

    @Test
    fun shouldFillCorridor() {
        for (i in 0..100) {
            val lab = createCorridor(10);

            LabFiller<FreeZone>().fillLab(lab, lab.first(), 2, 0);
            println(lab)
        }

    }

    @Test
    fun shouldFillBigCorridor() {

        val lab = createCorridor(100);

        LabFiller<FreeZone>().fillLab(lab, lab.first(), 10, 0);
        println(lab)


    }


    @Test
    fun shouldPlay() {
        val partie = initPartie()
        println(partie.level.map { it.toString() + "\n" })
        println("---")
        for (i in 0..100) {
            println("" + i + " " + partie.player)
            if (!partie.player.inventory.isEmpty())
                partie.player.selected = partie.player.inventory.random()
            playerInteractWith(partie, partie.player.location.content.random())

        }
        println("---")
        println(partie.level)

        println("---")
        println(partie.player.inventory.map { it.name })
    }


}
