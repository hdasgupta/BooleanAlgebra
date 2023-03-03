package com.digital.Digital.parser

import java.util.SortedMap
import java.util.SortedSet
import java.util.TreeMap

interface Expression {
    fun deepEquals(other: Expression): Boolean

    fun evaluate(variable: SortedMap<String, Boolean>): Boolean
}