package com.digital.Digital.parser

class Input(val name: String) : Expression {
    override fun deepEquals(other: Expression): Boolean =
        if(other is SubExpression) {
            false
        } else {
            name == (other as Input).name
        }

    override fun toString(): String = name
}