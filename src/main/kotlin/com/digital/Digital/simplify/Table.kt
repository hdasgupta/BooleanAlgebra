package com.digital.Digital.simplify

import java.util.*

class Table(val map:Map<String, EnumSet<WithNot>> = hashMapOf()):TreeMap<String, EnumSet<WithNot>>(map) {
    override fun equals(other: Any?): Boolean =
        if(other is Table) {
            keys.containsAll(other.keys) && other.keys.containsAll(keys)
        } else {
            false
        }
}