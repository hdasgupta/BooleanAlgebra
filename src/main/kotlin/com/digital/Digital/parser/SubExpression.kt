package com.digital.Digital.parser

import java.util.Collections
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

class SubExpression(val operator: Operator, val operands: MutableList<Expression>,) : Expression {
    override fun deepEquals(other: Expression): Boolean =
        if(other is SubExpression) {
            if(operator!=other.operator) {
                false
            } else if(operands.size != other.operands.size) {
                false
            } else {
                IntStream.range(0, operands.size)
                    .allMatch{operands[it].deepEquals(other.operands[it])}
            }
        } else {
            false
        }

    override fun toString(): String {
        if(operator == Operator.Not) {
            return "${operands[0]}\'"
        }
        val out = operands.stream()
            .map {
                if(it is SubExpression && it.operands.size>1 && it.operator == Operator.Or)
                    "($it)"
                else
                    it.toString()
            }
            .collect(Collectors.joining(operator.symbol))

        return if(operands.size>1 && operator==Operator.Or) {
            "($out)"
        } else {
            out
        }
    }
}