package com.digital.Digital.sop

import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.Input
import com.digital.Digital.parser.Operator
import com.digital.Digital.parser.SubExpression
import com.digital.Digital.simplify.Shorten
import com.digital.Digital.simplify.Table
import com.digital.Digital.simplify.WithNot
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

    fun canonical(expression: Expression): Expression {
        val vars = shorten.variables(expression)
        val tables : HashSet<Table> = shorten.createTable(expression)
        val newTables : HashSet<Table> = HashSet()

        for(table in tables) {
            val othersVars = vars.toMutableSet()
            othersVars.removeAll(table.keys)
            var _tables : HashSet<Table> = HashSet(listOf(table))
            for (v in othersVars) {
                _tables = _tables.stream()
                    .flatMap {
                        val t1 = Table(it)
                        val t2 = Table(it)

                        t1[v] = EnumSet.of(WithNot.N)
                        t2[v] = EnumSet.of(WithNot.Y)

                        Stream.of(t1, t2)
                    }
                    .toList()
                    .toHashSet()
            }

            newTables.addAll(_tables)
        }

        val inputs = shorten.variables(expression).stream()
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
                or.add(SubExpression(Operator.And, and))
            }
        }

        return if(or.size == 1) {
            or[0]
        } else {
            SubExpression(Operator.Or, or)
        }
    }

}