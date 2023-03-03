package com.digital.Digital.parser

import java.util.*

class Input(val name: String) : Expression {
    override fun deepEquals(other: Expression): Boolean =
        if(other is SubExpression) {
            false
        } else {
            name == (other as Input).name
        }

    override fun evaluate(variable: SortedMap<String, Boolean>): Boolean =
        variable[name] ?: false

    override fun toString(): String = name
}