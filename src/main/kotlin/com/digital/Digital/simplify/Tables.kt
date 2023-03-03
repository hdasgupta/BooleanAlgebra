package com.digital.Digital.simplify

import java.util.SortedSet
import java.util.TreeSet

class Tables(val variables: SortedSet<String>, val collection: Collection<Table> = listOf<Table>()): TreeSet<Table>(collection) {
    override fun add(element: Table): Boolean {
        element.variables(variables)
        return super.add(element)
    }
}