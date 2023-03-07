package com.digital.Digital.simplify


import com.digital.Digital.common.queue
import com.digital.Digital.common.shortQ
import com.digital.Digital.common.simpleQ
import java.util.stream.Collectors


import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.Input
import com.digital.Digital.parser.Operator
import com.digital.Digital.parser.SubExpression
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Comparator
import java.util.EnumSet
import java.util.SortedSet
import java.util.TreeSet

@Component
class Shorten {

    @Autowired
    lateinit var simplifyExpression: SimplifyExpression

    fun createTable(exp: Expression) : Tables {
        val tables = Tables(variables(exp))
        if (exp is SubExpression) {
            if (exp.operator == Operator.Or) {
                exp.operands.forEach {
                    val table = Table()
                    if(it is SubExpression) {
                        if(it.operator == Operator.Not && it.operands[0] is Input) {
                            if(table.containsKey((it.operands[0] as Input).name)) {
                                table[(it.operands[0] as Input).name]?.add(WithNot.N)
                            } else {
                                table[(it.operands[0] as Input).name]= EnumSet.of(WithNot.N)
                            }
                        } else {
                            varTable(it, table)
                        }

                    } else {
                        if(table.containsKey((it as Input).name)) {
                            table[it.name]?.add(WithNot.Y)
                        } else {
                            table[it.name]= EnumSet.of(WithNot.Y)
                        }
                    }
                    tables.add(table)
                }
            } else if(exp.operator == Operator.And) {
                val table = Table()
                varTable(exp, table)
                tables.add(table)
            } else if(exp.operator == Operator.Not) {
                val table = Table()
                val op = exp.operands[0] as Input
                if(table.containsKey(op.name)) {
                    table[op.name]?.add(WithNot.N)
                } else {
                    table[op.name]= EnumSet.of(WithNot.N)
                }
                tables.add(table)
            } else {
                val table = Table()
                if(table.containsKey((exp as Input).name)) {
                    table[exp.name]?.add(WithNot.Y)
                } else {
                    table[exp.name]= EnumSet.of(WithNot.Y)
                }
                tables.add(table)
            }
        }

        return tables
    }

    fun shorten(exp: Expression, steps: Steps) : Expression {
        if(shortQ[exp.toString()]!=null) {
            return shortQ[exp.toString()]!!
        }
        val tables  = createTable(exp)

        val inputs = variables(exp).stream()
            .collect(Collectors.toMap(fun(it)=it, fun(it)=Input(it)))

        var or : MutableList<Expression> = mutableListOf()

        val reason : MutableMap<Int, String> = mutableMapOf()

        for(i in tables.indices) {
            val map = tables.toList()[i]
            var and : MutableList<Expression> = mutableListOf()
            for(key in map.keys) {
                if(map[key]?.size == 1) {
                    if(map[key]?.contains(WithNot.N) == true) {
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
                or.add(simplifyExpression.and(and))
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

        if(!exp.deepEquals(ex)) {
            queue[exp.toString()] =exp
            queue[ex.toString()] = ex
            steps.add(Step(
                exp.toString(),
                ex.toString(),
                if(reason.isNotEmpty())

                        "Some parts are not required as "+
                        reason.values.stream().map { "$it.$it' = 0" }.toList()+
                        " respectfully."
                else
                    "Shorten Expression"
            ))
        }

        shortQ[exp.toString()] = ex

        return ex
    }

    fun varTable(exp: SubExpression, table: MutableMap<String, EnumSet<WithNot>>) =
        if(exp.operator == Operator.And) {
            for(op in exp.operands) {
                if(op is SubExpression) {
                    if(op.operator == Operator.Not) {
                        if(table.containsKey((op.operands[0] as Input).name)) {
                            table[(op.operands[0] as Input).name]?.add(WithNot.N)
                        } else {
                            table[(op.operands[0] as Input).name]= EnumSet.of(WithNot.N)
                        }

                    }
                } else {
                    if(table.containsKey((op as Input).name)) {
                        table[op.name]?.add(WithNot.Y)
                    } else {
                        table[op.name]= EnumSet.of(WithNot.Y)
                    }
                }
            }
        } else {

        }

    fun variables(exp: Expression) : SortedSet<String> =
        if(exp is SubExpression) {
            exp.operands
                .stream()
                .flatMap { variables(it).stream() }
                .toList()
                .toSortedSet()
        } else {
            sortedSetOf((exp as Input).name)
        }
}