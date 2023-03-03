package com.digital.Digital.simplify

import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.SubExpression
import java.util.Comparator
import java.util.stream.IntStream
import kotlin.streams.toList

class Steps(val exp:String, val steps: MutableList<Step> = mutableListOf()) {

    fun add(step: Step) : Any =
        if(steps.isNotEmpty()) {
            if(!steps.last().toExpression.contains(step.toExpression, false)) {
                step.currentExpression(this)
                steps.add(step)
            } else if(!steps.last().fromExpression.contains(step.fromExpression, false)) {
                step.currentExpression(this)
                steps.add(step)
            } else {

            }
        } else {
            step.currentExpression(this)
            steps.add(step)
        }


    fun prityPrint() {
        val out : List<List<String>> =
            steps.stream().map {
                listOf(
                    it.currentExpression,
                    it.fromExpression,
                    it.toExpression,
                    it.reason
                )
            }.toList()

        val widths = IntStream.range(0,4)
            .map {
                out.stream().map { list -> list[it].length }
                    .max(fun(i:Int,j:Int)= i.compareTo(j))
                    .get()+3
            }
            .toList()

        val headings = listOf("Current Step", "From Step", "To Step", "Reason")

        for(ind in headings.indices) {
            val pre = (widths[ind]-headings[ind].length)/2
            val post = widths[ind] - pre - headings[ind].length
            print(" ".repeat(pre))
            print(headings[ind])
            print(" ".repeat(post))

        }

        println()
        println()

        for(line in out) {
            print(line[0])
            print(" ".repeat(widths[0]-line[0].length))
            print(line[1])
            print(" ".repeat(widths[1]-line[1].length))
            print(line[2])
            print(" ".repeat(widths[2]-line[2].length))
            println(line[3])
        }
    }

}