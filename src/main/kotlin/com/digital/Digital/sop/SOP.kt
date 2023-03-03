package com.digital.Digital.sop

import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.Input
import com.digital.Digital.parser.Operator
import com.digital.Digital.parser.SubExpression
import com.digital.Digital.simplify.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.collections.HashSet
import kotlin.streams.toList

@Component
class SOP {
    @Autowired
    lateinit var shorten: Shorten

    @Autowired
    lateinit var simplifyExpression: SimplifyExpression

    fun canonical(expression: Expression): Expression {
        val vars = shorten.variables(expression)
        val tables = shorten.createTable(expression)
        val newTables  = Tables(vars)

        for(table in tables) {
            val othersVars = vars.toMutableSet()
            othersVars.removeAll(table.keys)
            var _tables  = Tables(vars)
            _tables.add(table)
            for (v in othersVars) {
                val t = _tables.stream()
                    .flatMap {
                        val t1 = Table(it)
                        val t2 = Table(it)

                        t1[v] = EnumSet.of(WithNot.N)
                        t2[v] = EnumSet.of(WithNot.Y)

                        Stream.of(t1, t2)
                    }
                    .toList()
                _tables = Tables(vars)
                _tables.addAll(t)

            }

            newTables.addAll(_tables)
        }

        val inputs = vars.stream()
            .collect(Collectors.toMap(fun(it)=it, fun(it)= Input(it)))

        var or : MutableList<Expression> = mutableListOf()

        for(i in newTables.indices) {
            val map = newTables.toList()[i]
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
                    break
                }
            }
            if(and.isNotEmpty()) {
                or.add(simplifyExpression.and(and))
            }
        }

        return if(or.size == 1) {
            or[0]
        } else {
            SubExpression(Operator.Or, or)
        }
    }

}