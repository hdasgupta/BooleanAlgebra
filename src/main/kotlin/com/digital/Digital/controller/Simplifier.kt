package com.digital.Digital.controller

import com.digital.Digital.parser.Parser
import com.digital.Digital.parser.Tokenizer
import com.digital.Digital.simplify.Shorten
import com.digital.Digital.simplify.SimplifyExpression
import com.digital.Digital.simplify.Step
import com.digital.Digital.simplify.Steps
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class Simplifier {
    @Autowired
    lateinit var simplifyExpression: SimplifyExpression

    @Autowired
    lateinit var shorten: Shorten

    @Autowired
    lateinit var parser: Parser

    @Autowired
    lateinit var tokenizer: Tokenizer

    @RequestMapping(value = ["/simplifier"])
    fun getTemplate(
        @RequestParam(required = false) formula: String? = null,
        map: ModelMap,
        req: HttpServletRequest
    ): String {
        map["formula"] = formula
        map["request"] = req

        return "Simplify"
    }

    @RequestMapping(value = ["/simpleHtml"])
    fun getDiffHtml(@RequestParam formula: String, map: ModelMap): String {
        try {
            val operand = parser.parse(tokenizer.parse(formula))
            val steps = Steps(operand.toString())
            val result = shorten.shorten(simplifyExpression.simplify(operand, steps), steps)
            val results = steps.steps
            map["formula"] = operand.toString()
            map["results"] = ArrayList(results)
            map["result"] = result.toString()
        } catch (t: Throwable) {
            map["results"] = ArrayList<Step>()
        }

        return "SimplifiedResult"
    }

}