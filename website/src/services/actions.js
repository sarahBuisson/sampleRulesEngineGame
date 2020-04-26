import {kotlinProxy} from "../util";
import labyrinth from 'labyrinth';

export const ACTION_TYPES = {UPDATE_STATE_TYPE: 'UPDATE_STATE', SELECT_OBJECT: 'SELECT_OBJECT'}

export function updateStateAction(statePath, data) {
    return {
        type: ACTION_TYPES.UPDATE_STATE_TYPE,
        payload: {statePath, data}
    }
}

export function newPartiePointNClick(size) {
    const newPartie = kotlinProxy(labyrinth.fr.perso.labyrinth.freezone.gameplay, false).initPartieExit(size);
    console.log(newPartie)
    return updateStateAction('currentPartie', newPartie)
}

export function newLab(size) {
    console.log(labyrinth.fr.perso.labyrinth)
    const newPartie = kotlinProxy(labyrinth.fr.perso.labyrinth.board.algorithm, false).initPartie(size);
    console.log(newPartie)
    return updateStateAction('currentLabPartie', newPartie)
}

export function newSudoku(size) {
    const newPartie = sudoku.initPartie(size);
    return updateStateAction('currentSudokuPartie', newPartie)
}
export function resoslveGrid(grid) {
    const newPartie = sudoku.fr.perso.sudoku.initPartie(size);
    return updateStateAction('currentSudokuPartie', newPartie)
}

export function newLabObj(size) {

    console.log(labyrinth.fr.perso.labyrinth)
    const newPartie = kotlinProxy(labyrinth.fr.perso.labyrinth.board.algorithm.composite, false).initPartieComposite(size);
    console.log(newPartie)
    return updateStateAction('currentLabObjPartie', newPartie)
}

export function selectObj(obj) {
    return updateStateAction('currentPartie.player.selected', obj)
}


export function play(partie, obj) {
    console.log("play")
    console.log(obj)
    console.log(partie)
    newPartiePointNClick = kotlinProxy(labyrinth.fr.perso.labyrinth.freezone.gameplay, false).playerInteractWith(partie, obj)
    console.log("after play")
    console.log(newPartiePointNClick)
    return updateStateAction('currentPartie', newPartiePointNClick)
}
