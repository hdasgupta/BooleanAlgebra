package com.digital.Digital.controller

import com.digital.Digital.tt.TruthTable
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigInteger
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.IntStream

@Controller
class TruthTable2Exp {

    @Autowired
    lateinit var truthTable:TruthTable

    @RequestMapping(value = ["/truthTable2Exp"])
    fun getTemplate(
        @RequestParam(required = false) variable: Int? = null,
        @RequestParam(required = false) output: String? = null,
        modelMap: ModelMap,
        req: HttpServletRequest
    ): String {

        modelMap["variables"] = listOf<String>()

        modelMap["results"] = listOf<SortedMap<String, Boolean>>()

        modelMap["formula"] = ""

        if (variable != null) {
            val count = BigInteger("2").pow(variable).toInt()

            var _output: String = output
                ?: IntStream.range(0, count)
                    .mapToObj { "0" }
                    .collect(Collectors.joining(""))

            val variables = IntStream.range(0, variable).mapToObj {
                'A'.plus(it).toString()
            }
                .toList()

            val all = truthTable.inputTruthTable(variables)

            modelMap["variables"] = variables

            modelMap["results"] = all

            if(count == _output.length) {
                for(i in all.indices) {
                    all[i]["output"] = _output[i] == '1'
                }

                val ones = all.filter { it["output"] == true }
                    .map {
                        variables.joinToString("") {
                            v->
                                if(it[v] == true) "1" else "0"
                        }
                    }
                    .stream()
                    .collect(Collectors.toMap(fun (v: String)=v, fun(_: String)=false))

                var map = IntStream.range(0, variable+1)
                    .mapToObj { BigInteger(it.toString()) }
                    .collect(
                        Collectors.toMap(
                            fun(i:BigInteger)=i.toInt(),
                            fun(i:BigInteger)=ones.keys.filter { it.count { c-> c=='1' } ==  i.toInt()}
                        )
                    )
                    .toSortedMap()

                var included = ones.toMutableMap()

                while (true) {
                    val newIncluded = sortedMapOf<String, Boolean>()
                    val newMap = sortedMapOf<Int, MutableList<String>>()
                    map.keys.map {
                        map[it]?.forEach { str ->
                            val similars = map[it + 1]?.filter { strNext ->
                                IntStream.range(0, str.length)
                                    .filter { i -> str[i] != strNext[i] }.count() == 1L
                            }
                            if (similars != null && similars.isNotEmpty()) {
                                similars.forEach {
                                    similar->
                                    val newStr = IntStream.range(0, str.length)
                                        .mapToObj { i -> if (str[i] == similar[i]) str[i].toString() else "_" }
                                        .collect(Collectors.joining(""))
                                    val index = newStr.count { c -> c == '1' }

                                    if (!newMap.containsKey(index)) {
                                        newMap[index] = mutableListOf()
                                    }
                                    included[str] = true
                                    included[similar] = true
                                    newIncluded[newStr] = false
                                    newMap[index]?.add(newStr)
                                }
                            } else {
                                if (included[str]!=true) {
                                    included[str] = true
                                    newIncluded[str]=false
                                    if (!newMap.containsKey(it)) {
                                        newMap[it] = mutableListOf()
                                    }
                                    newMap[it]?.add(str)
                                }
                            }
                        }
                    }

                    if(newIncluded.size == included.size && !newIncluded.keys.any { !included.containsKey(it) }) {
                        included = newIncluded
                        map = newMap as SortedMap<Int, List<String>>
                        break
                    }

                    included = newIncluded
                    map = newMap as SortedMap<Int, List<String>>
                }

                modelMap["formula"] = if(included.keys.size==1 && included.keys.toList()[0]=="__") {
                    "1"
                } else if(included.isEmpty()) {
                    "0"
                } else {
                    included.keys.map { str ->
                        IntStream.range(0, variable)
                            .filter { str[it] != '_' }
                            .mapToObj { variables[it] + if (str[it] == '1') "" else "'" }
                            .collect(Collectors.joining("."))
                    }.joinToString("+")
                }
            }

            modelMap["output"] = _output
        } else {
            modelMap["output"] = output
        }

        modelMap["variable"] = variable

        return "TruthTable2Exp"
    }
}