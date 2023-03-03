package com.digital.Digital.pos

import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.Input
import com.digital.Digital.parser.Operator
import com.digital.Digital.parser.SubExpression
import com.digital.Digital.simplify.*
import com.digital.Digital.sop.SOP
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.collections.HashSet

@Component
class POS {
    @Autowired
    lateinit var sop: SOP

    @Autowired
    lateinit var shorten: Shorten

    @Autowired
    lateinit var simplifyExpression: SimplifyExpression

    fun canonical(expression: Expression): Expression {
        val tables = shorten.createTable(
            sop.canonical(
                simplifyExpression.simplify(
                    expression,
                    Steps(expression.toString())
                )
            )
        )

        val allTables = allTables(shorten.variables(expression))

        allTables.removeAll(tables)

        val inputs = shorten.variables(expression).stream()
            .collect(Collectors.toMap(fun(it)=it, fun(it)= Input(it)))

        var and : MutableList<Expression> = mutableListOf()

        for(i in allTables.indices) {
            val map = allTables.toList()[i]
            var or : MutableList<Expression> = mutableListOf()
            for(key in map.keys) {
                if(map[key]?.size == 1) {
                    if(map[key]?.contains(WithNot.Y) == true) {
                        or.add(SubExpression(Operator.Not, mutableListOf(inputs[key]!!)))
                    } else {
                        or.add(inputs[key]!!)
                    }
                } else {
                    or = mutableListOf()
                    break
                }
            }
            if(or.isNotEmpty()) {
                and.add(SubExpression(Operator.Or, or))
            }
        }

        return if(and.size == 1) {
            and[0]
        } else {
            simplifyExpression.and(and)
        }


    }

    fun allTables(vars: SortedSet<String>): Tables {
        var tables = Tables(vars)

        vars.forEach {
            if(tables.isEmpty()) {
                val t1 = Table()
                val t2 = Table()

                t1[it] = EnumSet.of(WithNot.N)
                t2[it] = EnumSet.of(WithNot.Y)

                tables = Tables(vars)
                tables.add(t1)
                tables.add(t2)
            } else {
                val t = tables.stream().flatMap {
                        t->
                    val t1 = Table(t)
                    val t2 = Table(t)

                    t1[it] = EnumSet.of(WithNot.N)
                    t2[it] = EnumSet.of(WithNot.Y)

                    Stream.of(t1, t2)

                }
                    .toList()
                tables = Tables(vars)
                tables.addAll(t)
            }
        }

        return tables
    }
}