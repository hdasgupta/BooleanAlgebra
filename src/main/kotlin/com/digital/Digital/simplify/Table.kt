package com.digital.Digital.simplify

import java.util.*
import java.util.stream.Collectors

class Table(val map:Map<String, EnumSet<WithNot>> = hashMapOf()):TreeMap<String, EnumSet<WithNot>>(map), Comparable<Table> {
    private lateinit var variables : SortedSet<String>

    fun variables(variables : SortedSet<String>) {
        this.variables = variables
    }
    override fun compareTo(other: Table): Int {
        val _this = this
        val str1 = variables.stream()
            .map {
                if(_this.contains(it)) {
                    if(_this[it]?.contains(WithNot.N) == true) "1" else "0"
                } else {
                    "_"
                }
            }
            .collect(Collectors.joining(""))

        val str2 = variables.stream()
            .map {
                if(other.contains(it)) {
                    if(other[it]?.contains(WithNot.N) == true) "1" else "0"
                } else {
                    "_"
                }
            }
            .collect(Collectors.joining(""))

        return str1.compareTo(str2)
    }

    override fun equals(other: Any?): Boolean =
        if(other is Table) {
            keys.containsAll(other.keys) && other.keys.containsAll(keys)
        } else {
            false
        }
}