package com.digital.Digital.parser

import java.util.*
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

    override fun evaluate(variable: SortedMap<String, Boolean>): Boolean {
        val all = operands.stream().map {
            if(it is SubExpression)
                it.evaluate(variable)
            else
                variable[(it as Input).name] ?: false
        }.toList()
        var result = all[0]
        when(operator) {
            Operator.Not->
                result = result.not()
            Operator.Or ->
                IntStream.range(1, all.size)
                    .forEach {
                        result = result.or(all[it])
                    }
            Operator.And ->
                IntStream.range(1, all.size)
                    .forEach {
                        result = result.and(all[it])
                    }
            else -> {

            }
        }

        return result

    }


    override fun toString(): String {
        if(operator == Operator.Not) {
            return "${operands[0]}\'"
        }
        val out = operands.stream()
            .map {
                //if(it is SubExpression && it.operands.size>1 && it.operator == Operator.Or)
                    "$it"
                //else
                //    it.toString()
            }
            .collect(Collectors.joining(operator.symbol))

        return if(operands.size>1 && operator==Operator.Or) {
            "($out)"
        } else {
            out
        }
    }
}