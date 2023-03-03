package com.digital.Digital.simplify

import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.Parser
import com.digital.Digital.parser.SubExpression
import com.digital.Digital.parser.Tokenizer
import org.springframework.beans.factory.annotation.Autowired

class Step (var fromExpression: String, val toExpression: String, val reason: String) {

    @Autowired
    var  parser = Parser()

    lateinit var currentExpression:String

    fun currentExpression(steps: Steps) {
        this.fromExpression = replaceFrom(steps)
        this.currentExpression = replaceCurrent(steps)
    }

    private fun replaceCurrent(steps: Steps) : String {
        var currentExpression = steps.exp
        for(step in steps.steps) {
            currentExpression = replace(currentExpression, step.fromExpression, step.toExpression)
        }
        return replace(currentExpression, fromExpression, toExpression)
    }

    private fun replaceFrom(steps: Steps) : String {
        var fromExpression = this.fromExpression
        for(index in steps.steps.indices) {
            val step = steps.steps[index]
            fromExpression = replace(fromExpression, step.fromExpression, step.toExpression)
        }
        return fromExpression
    }


    private fun replace(exp: String, fromExpression: String, toExpression: String): String =

                exp.toString()
                    .replace(
                        fromExpression,
                        toExpression
                    )
                    .replace("*([^a-zA-Z0-9]\\.|\\(\\.|\\.\\)|\\.[^a-zA-Z0-9])*", "")


}
