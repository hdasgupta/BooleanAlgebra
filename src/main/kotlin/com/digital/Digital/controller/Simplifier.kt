package com.digital.Digital.controller

import com.digital.Digital.common.queue
import com.digital.Digital.common.stepsQ
import com.digital.Digital.parser.Expression
import com.digital.Digital.parser.Parser
import com.digital.Digital.parser.Tokenizer
import com.digital.Digital.pos.POS
import com.digital.Digital.simplify.Shorten
import com.digital.Digital.simplify.SimplifyExpression
import com.digital.Digital.simplify.Step
import com.digital.Digital.simplify.Steps
import com.digital.Digital.sop.SOP
import com.digital.Digital.tt.TruthTable
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.yaml.snakeyaml.util.UriEncoder
import java.net.URLDecoder
import java.util.concurrent.PriorityBlockingQueue

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

    @Autowired
    lateinit var sop: SOP

    @Autowired
    lateinit var pos : POS

    @Autowired
    lateinit var truthTable: TruthTable

    @RequestMapping(value = ["/simplifier"])
    fun getTemplate(
        @RequestParam(required = false) formula: String? = null,
        @RequestParam(required = true) page: String,
        map: ModelMap,
        req: HttpServletRequest
    ): String {
        map["formula"] = formula
        map["request"] = req
        map["page"] = page

        return "Simplify"
    }

    @RequestMapping(value = ["/simpleSOPHtml"])
    fun getDiffHtml(
        @RequestParam formula: String,
        map: ModelMap,
        req: HttpServletRequest): String {
        try {
            val operand = parser.parse(tokenizer.parse(formula.replace("&#39;", "'")))
            var steps:Steps =  Steps(operand.toString())
            val simpl = simplifyExpression.simplify(operand, steps)
            queue[simpl.toString()] = simpl
            val result = shorten.shorten(simpl, steps)
            queue[result.toString()] = result

            if(stepsQ[formula.replace("&#39;", "'")]!=null) {
                steps = stepsQ[formula.replace("&#39;", "'")]!!
            } else {
                stepsQ[formula.replace("&#39;", "'")] = steps
            }
            val results = steps.steps

            map["request"] = req
            map["formula"] = operand.toString()
            map["results"] = ArrayList(results)
            map["result"] = result.toString()
            val canonical = sop.canonical(result)
            queue[canonical.toString()] = canonical
            map["canonical"] = canonical.toString()
        } catch (t: Throwable) {
            map["results"] = ArrayList<Step>()
        }

        return "SimplifiedResult"
    }

    @RequestMapping(value = ["/simplePOSHtml"])
    fun getPOSHtml(
        @RequestParam formula: String,
        map: ModelMap,
        req: HttpServletRequest): String {
        try {
            val operand = parser.parse(tokenizer.parse(formula.replace("&#39;", "'")))
            var steps = Steps(operand.toString())
            val simpl = simplifyExpression.simplify(operand, steps)
            queue[simpl.toString()] = simpl
            val result = shorten.shorten(simpl, steps)
            queue[result.toString()] = result

            if(stepsQ[formula.replace("&#39;", "'")]!=null) {
                steps = stepsQ[formula.replace("&#39;", "'")]!!
            } else {
                stepsQ[formula.replace("&#39;", "'")] = steps
            }

            val results = steps.steps
            map["request"] = req
            map["formula"] = operand.toString()
            map["results"] = ArrayList(results)
            map["result"] = result.toString()
            val canonical = pos.canonical(result)
            queue[canonical.toString()] = canonical
            map["canonical"] = canonical.toString()
        } catch (t: Throwable) {
            map["results"] = ArrayList<Step>()
        }

        return "SimplifiedPOSResult"
    }

    @RequestMapping(value = ["/simpleTruth TableHtml"])
    fun getTTHtml(@RequestParam formula: String, map: ModelMap): String {
        try {
            val operand = parser.parse(tokenizer.parse(formula.replace("&#39;", "'")))
            queue[formula.replace("&#39;", "'")] = operand
            val steps = Steps(operand.toString())
            val simpl = simplifyExpression.simplify(operand, steps)
            queue[simpl.toString()] = simpl
            val result = shorten.shorten(simpl, steps)
            queue[result.toString()] = result

            if(stepsQ[formula.replace("&#39;", "'")]!=null) {

            } else {
                stepsQ[formula.replace("&#39;", "'")] = steps
            }
            val results = truthTable.get(result)
            map["formula"] = operand.toString()
            map["variables"] = shorten.variables(operand)
            map["results"] = results
        } catch (t: Throwable) {
            map["results"] = ArrayList<Step>()
        }

        return "truthTable"
    }

}