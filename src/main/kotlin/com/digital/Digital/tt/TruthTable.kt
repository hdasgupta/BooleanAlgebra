package com.digital.Digital.tt

import com.digital.Digital.parser.Expression
import com.digital.Digital.simplify.Shorten
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.SortedMap
import java.util.SortedSet
import java.util.TreeMap

@Component
class TruthTable {

    @Autowired
    lateinit var shorten: Shorten

    fun get(expression: Expression): MutableList<SortedMap<String, Boolean>> {
        val variables = shorten.variables(expression)

        val all = inputTruthTable(variables.toList())

        for(map in all) {
            map["output"] = expression.evaluate(map)
        }

        return all
    }

    fun inputTruthTable(variables: List<String>, i:Int = 0, map:SortedMap<String, Boolean> = sortedMapOf(), all:MutableList<SortedMap<String, Boolean>> = mutableListOf()): MutableList<SortedMap<String, Boolean>> {
        if(i==variables.size) {
            all.add(map.toSortedMap())
            return all
        } else {
            map[variables[i]]=false
            inputTruthTable(variables, i+1, map, all)
            map[variables[i]]=true
            inputTruthTable(variables, i+1, map, all)
            return all
        }
    }
}