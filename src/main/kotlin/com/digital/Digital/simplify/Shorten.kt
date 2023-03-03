package com.digital.Digital.simplify


import java.util.stream.Collectors


import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.Input
import com.digital.Digital.parser.Operator
import com.digital.Digital.parser.SubExpression
import org.springframework.stereotype.Component
import java.util.Comparator
import java.util.EnumSet
import java.util.TreeSet

@Component
class Shorten {
    fun shorten(exp: Expression, steps: Steps) : Expression {
        val tables: MutableList<Map<String, EnumSet<WithNot>>> = mutableListOf()
        if (exp is SubExpression) {
            if (exp.operator == Operator.Or) {
                exp.operands.forEach {
                    val table : MutableMap<String, EnumSet<WithNot>> = mutableMapOf()
                    if(it is SubExpression) {
                        varTable(it, table)
                    } else {
                        if(table.containsKey((it as Input).name)) {
                            table[it.name]?.add(WithNot.N)
                        } else {
                            table[it.name]= EnumSet.of(WithNot.N)
                        }
                    }
                    tables.add(table.toSortedMap())
                }
            } else if(exp.operator == Operator.And) {
                val table : MutableMap<String, EnumSet<WithNot>> = mutableMapOf()
                varTable(exp, table)
                tables.add(table.toSortedMap())
            } else if(exp.operator == Operator.Not) {
                val table : MutableMap<String, EnumSet<WithNot>> = mutableMapOf()
                val op = exp.operands[0] as Input
                if(table.containsKey(op.name)) {
                    table[op.name]?.add(WithNot.Y)
                } else {
                    table[op.name]= EnumSet.of(WithNot.Y)
                }
                tables.add(table.toSortedMap())
            } else {
                val table : MutableMap<String, EnumSet<WithNot>> = mutableMapOf()
                if(table.containsKey((exp as Input).name)) {
                    table[exp.name]?.add(WithNot.N)
                } else {
                    table[exp.name]= EnumSet.of(WithNot.N)
                }
                tables.add(table.toSortedMap())
            }
        }

        val inputs = variables(exp).stream()
            .collect(Collectors.toMap(fun(it)=it, fun(it)=Input(it)))

        var or : MutableList<Expression> = mutableListOf()

        val reason : MutableMap<Int, String> = mutableMapOf()

        for(i in tables.indices) {
            val map = tables[i]
            var and : MutableList<Expression> = mutableListOf()
            for(key in map.keys) {
                if(map[key]?.size == 1) {
                    if(map[key]?.contains(WithNot.Y) == true) {
                        and.add(SubExpression(Operator.Not, mutableListOf(inputs[key]!!)))
                    } else {
                        and.add(inputs[key]!!)
                    }
                } else {
                    and = mutableListOf()
                    reason[i] = key
                    break
                }
            }
            if(and.isNotEmpty()) {
                or.add(SubExpression(Operator.And, and))
            }
        }

        val set = TreeSet(Comparator.comparing(fun(exp:Expression)=exp.toString()))
        set.addAll(or)
        or = set.toMutableList()

        val ex = if(or.size == 1) {
            or[0]
        } else {
            SubExpression(Operator.Or, or)
        }

        if(reason.isNotEmpty()) {
            steps.add(Step(
                exp.toString(),
                ex.toString(),

                        "Some parts are not required as "+
                        reason.values.stream().map { "$it.$it' = 0" }.toList()+
                        " respectfully."
            ))
        }

        return ex
    }

    fun varTable(exp: SubExpression, table: MutableMap<String, EnumSet<WithNot>>) =
        if(exp.operator == Operator.And) {
            for(op in exp.operands) {
                if(op is SubExpression) {
                    if(op.operator == Operator.Not) {
                        if(table.containsKey((op.operands[0] as Input).name)) {
                            table[(op.operands[0] as Input).name]?.add(WithNot.Y)
                        } else {
                            table[(op.operands[0] as Input).name]= EnumSet.of(WithNot.Y)
                        }

                    }
                } else {
                    if(table.containsKey((op as Input).name)) {
                        table[op.name]?.add(WithNot.N)
                    } else {
                        table[op.name]= EnumSet.of(WithNot.N)
                    }
                }
            }
        } else {

        }

    fun variables(exp: Expression) : Set<String> =
        if(exp is SubExpression) {
            exp.operands
                .stream()
                .flatMap { variables(it).stream() }
                .toList()
                .toSet()
        } else {
            setOf((exp as Input).name)
        }
}