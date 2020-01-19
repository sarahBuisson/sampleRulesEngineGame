package fr.perso.rules

import fr.perso.SCase
import fr.perso.SCasePossible


data class UnicityError<T>(val value: T, val locations: List<SCase<T>>) {
    override fun toString() = "$value not unique :$locations"
}


fun <T> respectUnicity(ensemble: Iterable<SCase<T>>, values: List<T>): List<UnicityError<T>> {

    return values.map { value ->

        val filter = ensemble.filter { it.getValue() == value }
        if (filter.size > 1) {
            UnicityError(value, filter)
        } else {
            null
        }

    }.filterNotNull()
}


data class PresenceError<T>(val value: T) {
    override fun toString() = "$value not found"
}

fun <T> respectPresence(ensemble: Collection<SCase<T>>, values: List<T>): List<PresenceError<T>> {

    return values.map { value ->
        if (ensemble.none { case -> case.getValue() == value }) {
            PresenceError(value)
        } else {
            null
        }
    }.filterNotNull()


}

fun <T> respectPossiblePresence(ensemble: Iterable<SCasePossible<T>>, values: List<T>): List<PresenceError<T>> {

    return values.map { value ->
        if (ensemble.flatMap { it.getPossibles() }.none { cv -> cv == value }) {
            PresenceError(value)
        } else {
            null
        }
    }.filterNotNull()
}
