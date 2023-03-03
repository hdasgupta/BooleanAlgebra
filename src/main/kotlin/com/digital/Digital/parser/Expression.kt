package com.digital.Digital.parser

interface Expression {
    fun deepEquals(other: Expression): Boolean
}